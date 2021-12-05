package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.id.IdGenerationContext;
import com.github.mygreen.sqlmapper.core.id.IdGenerator;
import com.github.mygreen.sqlmapper.core.id.IdentityIdGenerator;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.core.query.JdbcTemplateBuilder;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.util.NumberConvertUtils;
import com.github.mygreen.sqlmapper.core.util.QueryUtils;


/**
 * 挿入を行うSQLを自動生成するクエリを実行します。
 * {@link AutoInsertImpl}のクエリ実行処理の移譲先です。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public class AutoInsertExecutor {

    /**
     * バージョンプロパティの初期値
     */
    public static final long INITIAL_VERSION = 1L;

    /**
     * クエリ情報
     */
    private final AutoInsertImpl<?> query;

    /**
     * 設定情報
     */
    private final SqlMapperContext context;

    /**
     * クエリのパラメータです。
     */
    private final MapSqlParameterSource paramSource = new MapSqlParameterSource();

    /**
     * 挿入するカラム名
     */
    private List<String> usingColumnNames = new ArrayList<>();

    /**
     * IDENTITYによる主キーの自動生成を使用するカラム名
     */
    private List<String> usingIdentityKeyColumnNames = new ArrayList<>();

    /**
     * レコードの挿入操作を行う処理
     */
    private SimpleJdbcInsert insertOperation;

    /**
     * 組み立てたクエリ情報を指定するコンストラクタ。
     * @param query クエリ情報
     */
    public AutoInsertExecutor(AutoInsertImpl<?> query) {
        this.query = query;
        this.context = query.getContext();
    }

    /**
     * クエリ実行の準備を行います。
     */
    private void prepare() {

        prepareSqlParam();
        prepareInsertOperation();
    }

    /**
     * SQLのパラメータを準備します。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareSqlParam() {

        for(PropertyMeta propertyMeta : query.getEntityMeta().getAllColumnPropertyMeta()) {

            if(!isTargetProperty(propertyMeta)) {
                continue;
            }

            final String columnName = propertyMeta.getColumnMeta().getName();

            // パラメータの組み立て
            Object propertyValue = PropertyValueInvoker.getEmbeddedPropertyValue(propertyMeta, query.getEntity());

            Optional<GenerationType> generationType = propertyMeta.getIdGenerationType();
            if(propertyMeta.isId() && generationType.isPresent()) {
                if(generationType.get() == GenerationType.IDENTITY) {
                    //IDENTITYの場合は、クエリ実行後に取得するため、対象のカラム情報を一時保存しておく。
                    usingIdentityKeyColumnNames.add(columnName);
                    continue;
                } else {
                    propertyValue = getNextVal(propertyMeta);
                    PropertyValueInvoker.setEmbeddedPropertyValue(propertyMeta, query.getEntity(), propertyValue);
                }
            }

            if(propertyValue == null && propertyMeta.isVersion()) {
                // バージョンキーが設定されていない場合、初期値設定する
                propertyValue = NumberConvertUtils.convertNumber(propertyMeta.getPropertyType(), INITIAL_VERSION);
                PropertyValueInvoker.setEmbeddedPropertyValue(propertyMeta, query.getEntity(), propertyValue);

            }

            // IDENTITYの主キーでない場合は通常カラムとして追加
            if(!usingIdentityKeyColumnNames.contains(columnName)) {
                usingColumnNames.add(columnName);
            }

            // クエリのパラメータの組み立て
            ValueType valueType = propertyMeta.getValueType();
            paramSource.addValue(columnName, valueType.getSqlParameterValue(propertyValue));

        }
    }


    /**
     * 挿入対象のプロパティか判定します。
     * @param propertyMeta プロパティ情報
     * @return 挿入対象のとき、{@literal true} を返します。
     */
    private boolean isTargetProperty(final PropertyMeta propertyMeta) {

        if(propertyMeta.isId()) {
            return true;
        }

        if(propertyMeta.isTransient()) {
            return false;
        }

        final String propertyName = propertyMeta.getName();

        if(query.getIncludesProperties().contains(propertyName)) {
            return true;
        }

        if(query.getExcludesProperties().contains(propertyName)) {
            return false;
        }

        // 挿入対象が指定されているときは、その他はすべて抽出対象外とする。
        return query.getIncludesProperties().isEmpty();

    }


    /**
     * 主キーを生成する
     * @param propertyMeta 生成対象のIDプロパティのメタ情報
     * @return 生成した主キーの値
     */
    private Object getNextVal(final PropertyMeta propertyMeta) {

        IdGenerator generator = propertyMeta.getIdGenerator().get();
        IdGenerationContext generationContext = propertyMeta.getIdGenerationContext().get();

        // トランザクションは別にする。
        return context.txRequiresNew().execute(action -> {
            return generator.generateValue(generationContext);
        });
    }

    /**
     * SQLの実行をする処理を組み立てます
     */
    private void prepareInsertOperation() {

        this.insertOperation = new SimpleJdbcInsert(getJdbcTemplate())
                .withTableName(query.getEntityMeta().getTableMeta().getFullName())
                .usingColumns(QueryUtils.toArray(usingColumnNames));

        if(!usingIdentityKeyColumnNames.isEmpty()) {
            insertOperation.usingGeneratedKeyColumns(QueryUtils.toArray(usingIdentityKeyColumnNames));
        }
    }

    /**
     * {@link JdbcTemplate}を取得します。
     * @return {@link JdbcTemplate}のインスタンス。
     */
    private JdbcTemplate getJdbcTemplate() {
        return JdbcTemplateBuilder.create(context.getDataSource(), context.getJdbcTemplateProperties())
                .queryTimeout(query.getQueryTimeout())
                .build();
    }

    /**
     * 挿入の実行
     * @return 更新した行数
     * @throws DuplicateKeyException 主キーなどのが一意制約に違反したとき。
     */
    public int execute() {
        prepare();

        if(this.usingIdentityKeyColumnNames.isEmpty()) {
            // 主キーがIDENTITYによる生成しない場合
            return insertOperation.execute(paramSource);

        } else {
            final KeyHolder keyHolder = insertOperation.executeAndReturnKeyHolder(paramSource);

            // 生成した主キーをエンティティに設定する
            for(Map.Entry<String, Object> entry : keyHolder.getKeys().entrySet()) {

                if(!usingIdentityKeyColumnNames.contains(entry.getKey())) {
                    continue;
                }

                PropertyMeta propertyMeta = query.getEntityMeta().getColumnPropertyMeta(entry.getKey()).orElseThrow();
                IdentityIdGenerator idGenerator = (IdentityIdGenerator) propertyMeta.getIdGenerator().get();
                Object propertyValue = idGenerator.generateValue((Number)entry.getValue());
                PropertyValueInvoker.setEmbeddedPropertyValue(propertyMeta, query.getEntity(), propertyValue);

            }

            return 1;
        }

    }
}

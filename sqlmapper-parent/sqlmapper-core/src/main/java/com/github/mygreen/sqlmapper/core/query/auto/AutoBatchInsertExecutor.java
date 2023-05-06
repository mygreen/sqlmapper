package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameterValue;
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
import com.github.mygreen.sqlmapper.core.query.InsertClause;
import com.github.mygreen.sqlmapper.core.query.JdbcTemplateBuilder;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.util.NumberConvertUtils;
import com.github.mygreen.sqlmapper.core.util.QueryUtils;


/**
 * バッチ挿入を行うSQLを自動生成するクエリを実行します。
 * {@link AutoBatchInsertImpl}のクエリ実行処理の移譲先です。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public class AutoBatchInsertExecutor {

    /**
     * バージョンプロパティの初期値
     */
    public static final long INITIAL_VERSION = AutoInsertExecutor.INITIAL_VERSION;

    /**
     * クエリ情報
     */
    private final AutoBatchInsertImpl<?> query;

    /**
     * 設定情報
     */
    private final SqlMapperContext context;

    /**
     * INSERT句 - SQLログ出力のために使用する。
     */
    private InsertClause insertClause = new InsertClause();

    /**
     * クエリのパラメータ - エンティティごとの設定
     */
    private MapSqlParameterSource[] batchParams;

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
     * シーケンスやテーブルによる主キーの生成したキー
     * <p>key=カラム名、value=全レコード分の生成したキーの値</p>
     */
    private Map<String, Object[]> generatedKeysMap = new HashMap<String, Object[]>();

    /**
     * 組み立てたクエリ情報を指定するコンストラクタ。
     * @param query クエリ情報
     */
    public AutoBatchInsertExecutor(AutoBatchInsertImpl<?> query) {
        this.query = query;
        this.context = query.getContext();
    }

    /**
     * クエリ実行の準備を行います。
     */
    private void prepare() {

        this.batchParams = new MapSqlParameterSource[query.getEntitySize()];

        prepareSqlParam();
        prepareInsertOperation();
    }

    /**
     * クエリ実行時のパラメータを準備します。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareSqlParam() {

        final int dataSize = query.getEntitySize();

        for(PropertyMeta propertyMeta : query.getEntityMeta().getAllColumnPropertyMeta()) {

            if(!isTargetProperty(propertyMeta)) {
                continue;
            }

            final String columnName = propertyMeta.getColumnMeta().getName();

            // 各レコードのパラメータを作成する。
            for(int i=0; i < dataSize; i++) {
                final MapSqlParameterSource paramSource = QueryUtils.get(batchParams, i);
                Object propertyValue = PropertyValueInvoker.getEmbeddedPropertyValue(propertyMeta, query.getEntity(i));

                Optional<GenerationType> generationType = propertyMeta.getIdGenerationType();
                if(propertyMeta.isId() && generationType.isPresent()) {
                    if(generationType.get() == GenerationType.IDENTITY) {
                        if(i == 0) {
                            //IDENTITYの場合は、クエリ実行後に取得するため、対象のカラム情報を１回だけ
                            usingIdentityKeyColumnNames.add(columnName);
                        }
                        continue;
                    } else {
                        propertyValue = getNextVal(propertyMeta, i);
                        PropertyValueInvoker.setEmbeddedPropertyValue(propertyMeta, query.getEntity(i), propertyValue);
                    }
                }

                if(propertyValue == null && propertyMeta.isVersion()) {
                    // バージョンキーが設定されていない場合、初期値設定する
                    propertyValue = NumberConvertUtils.convertNumber(propertyMeta.getPropertyType(), INITIAL_VERSION);
                    PropertyValueInvoker.setEmbeddedPropertyValue(propertyMeta, query.getEntity(i), propertyValue);

                }

                // クエリのパラメータの組み立て
                ValueType valueType = propertyMeta.getValueType();
                Object paramValue = valueType.getSqlParameterValue(propertyValue);
                if(paramValue instanceof SqlParameterValue) {
                    // SimpleJdbcInsert を使用する際は、テーブルのコンテキストを見るので、型情報は不要。
                    paramValue = ((SqlParameterValue)paramValue).getValue();
                }
                paramSource.addValue(columnName, paramValue);

            }

            // IDENTITYの主キーでない場合は通常カラムとして追加
            if(!usingIdentityKeyColumnNames.contains(columnName)) {
                usingColumnNames.add(columnName);

                if(context.getSqlLogger().getProp().isEnabled()) {
                    insertClause.addSql(columnName, "?");
                }
            }

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

        if(propertyMeta.isVersion()) {
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
     * @param columnName 生成対象のカラム名
     * @param index 生成対象のレコードのインデックス
     * @return 生成した主キーの値
     */
    private Object getNextVal(final PropertyMeta propertyMeta, final int index) {

        IdGenerator generator = propertyMeta.getIdGenerator().get();
        String columnName = propertyMeta.getColumnMeta().getName();
        IdGenerationContext generationContext = propertyMeta.getIdGenerationContext().get();

        // 1レコードの主キーをまとめてキーを生成しておき、キャッシュしておく。
        Object[] generatedKeys = generatedKeysMap.computeIfAbsent(columnName, v ->
                context.txRequiresNew().execute(action -> {
                    return generator.generateValues(generationContext, query.getEntities().length);
                }));

         return generatedKeys[index];
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
     * 挿入処理を実行します。
     * @return 挿入したレコード件数を返します。
     * @throws DuplicateKeyException 主キーなどのが一意制約に違反したとき。
     */
    public int[] execute() {

        prepare();

        if(context.getSqlLogger().getProp().isEnabled()) {
            String executedSql = "insert into "
                    + query.getEntityMeta().getTableMeta().getFullName()
                    + insertClause.toIntoSql()
                    + insertClause.toValuesSql();

            List<Object[]> batchArgs = new ArrayList<>(batchParams.length);
            for(MapSqlParameterSource paramSource : batchParams) {
                batchArgs.add(paramSource.getValues().values().toArray());
            }
            context.getSqlLogger().outBatch(executedSql, batchArgs);
        }

        if(this.usingIdentityKeyColumnNames.isEmpty()) {
            // 主キーがIDENTITYによる生成でない場合
            return insertOperation.executeBatch(batchParams);

        } else {
            // １件ずつ処理する
            int dataSize = query.getEntities().length;
            int[] res = new int[dataSize];
            for(int i=0; i < dataSize; i++) {

                final KeyHolder keyHolder = insertOperation.executeAndReturnKeyHolder(batchParams[i]);
                // 生成した主キーをエンティティに設定する
                for(Map.Entry<String, Object> entry : keyHolder.getKeys().entrySet()) {

                    if(!usingIdentityKeyColumnNames.contains(entry.getKey())) {
                        continue;
                    }

                    PropertyMeta propertyMeta = query.getEntityMeta().getColumnPropertyMeta(entry.getKey()).orElseThrow();
                    IdentityIdGenerator idGenerator = (IdentityIdGenerator) propertyMeta.getIdGenerator().get();
                    Object propertyValue = idGenerator.generateValue((Number)entry.getValue());
                    PropertyValueInvoker.setEmbeddedPropertyValue(propertyMeta, query.getEntity(i), propertyValue);

                }

                res[i] = 1;

            }

            return res;
        }
    }
}

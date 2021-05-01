package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.id.IdGenerator;
import com.github.mygreen.sqlmapper.core.id.IdentityIdGenerator;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.util.NumberConvertUtils;
import com.github.mygreen.sqlmapper.core.util.QueryUtils;

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

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareSqlParam() {

        for(PropertyMeta propertyMeta : query.getEntityMeta().getAllColumnPropertyMeta()) {

            final String propertyName = propertyMeta.getName();
            if(!propertyMeta.getColumnMeta().isInsertable()) {
                continue;
            }

            if(query.getExcludesProperties().contains(propertyName)) {
                continue;
            }

            if(!query.getIncludesProperties().isEmpty() && !query.getIncludesProperties().contains(propertyName)) {
                continue;
            }

            final String columnName = propertyMeta.getColumnMeta().getName();

            // パラメータの組み立て
            Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity());

            Optional<GenerationType> generationType = propertyMeta.getIdGenerationType();
            if(propertyMeta.isId() && generationType.isPresent()) {
                if(generationType.get() == GenerationType.IDENTITY) {
                    //IDENTITYの場合は、クエリ実行後に取得するため、対象のカラム情報を一時保存しておく。
                    usingIdentityKeyColumnNames.add(columnName);
                    continue;
                } else {
                    propertyValue = getNextVal(propertyMeta.getIdGenerator().get());
                    PropertyValueInvoker.setPropertyValue(propertyMeta, query.getEntity(), propertyValue);
                }
            }

            if(propertyValue == null && propertyMeta.isVersion()) {
                // バージョンキーが設定されていない場合、初期値設定する
                propertyValue = NumberConvertUtils.convertNumber(propertyMeta.getPropertyType(), INITIAL_VERSION);
                PropertyValueInvoker.setPropertyValue(propertyMeta, query.getEntity(), propertyValue);

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
     * 主キーを生成する
     * @param generator 主キーの生成処理
     * @return 生成した主キーの値
     */
    private Object getNextVal(final IdGenerator generator) {

        // トランザクションは別にする。
        return context.getRequiresNewTransactionTemplate().execute(action -> {
            return generator.generateValue();
        });
    }

    /**
     * SQLの実行をする処理を組み立てます
     */
    private void prepareInsertOperation() {

        this.insertOperation = new SimpleJdbcInsert(context.getJdbcTemplate())
                .withTableName(query.getEntityMeta().getTableMeta().getFullName())
                .usingColumns(QueryUtils.toArray(usingColumnNames));

        if(!usingIdentityKeyColumnNames.isEmpty()) {
            insertOperation.usingGeneratedKeyColumns(QueryUtils.toArray(usingIdentityKeyColumnNames));
        }
    }

    /**
     * 挿入の実行
     * @return 更新した行数
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
                PropertyValueInvoker.setPropertyValue(propertyMeta, query.getEntity(), propertyValue);

            }

            return 1;
        }

    }
}

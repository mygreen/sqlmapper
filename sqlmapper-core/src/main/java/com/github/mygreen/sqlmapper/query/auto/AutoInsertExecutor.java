package com.github.mygreen.sqlmapper.query.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.id.IdGenerator;
import com.github.mygreen.sqlmapper.id.IdentityIdGenerator;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.query.InsertClause;
import com.github.mygreen.sqlmapper.query.QueryExecutorBase;
import com.github.mygreen.sqlmapper.type.ValueType;
import com.github.mygreen.sqlmapper.util.NumberConvertUtils;

public class AutoInsertExecutor extends QueryExecutorBase {

    /**
     * バージョンプロパティの初期値
     */
    public static final long INITIAL_VERSION = 1L;

    private final AutoInsert<?> query;

    /**
     * INSERTのINTO句とVALUES句
     */
    private InsertClause insertClause = new InsertClause();

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータ
     */
    private MapSqlParameterSource paramSource = new MapSqlParameterSource();

    /**
     * IDENTITYによる主キーの自動生成を使用するカラム名
     */
    private List<String> usingIdentityGeneratedColumnNames = new ArrayList<>();

    public AutoInsertExecutor(AutoInsert<?> query) {
        super(query.getContext());
        this.query = query;
    }

    @Override
    public void prepare() {

        prepareInsertClause();

        prepareSql();

        completed();

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareInsertClause() {

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

            final String paramName = "_" + propertyName;

            // IN句の組み立て
            this.insertClause.addSql(propertyMeta.getColumnMeta().getName(), ":" + paramName);

            // パラメータの組み立て
            Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity());

            Optional<GenerationType> generationType = propertyMeta.getIdGenerationType();
            if(propertyMeta.isId() && generationType.isPresent()) {
                if(generationType.get() == GenerationType.IDENTITY) {
                    //IDENTITYの場合は、クエリ実行後に取得するため、対象のカラム情報を一時保存しておく。
                    usingIdentityGeneratedColumnNames.add(propertyMeta.getColumnMeta().getName());
                    propertyValue = null;
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

            // クエリのパラメータの組み立て
            ValueType valueType = context.getDialect().getValueType(propertyMeta);
            valueType.bindValue(propertyValue, paramSource, paramName);

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
     * 実行するSQLを組み立てます
     */
    private void prepareSql() {
        final String sql = "INSERT INTO "
                + query.getEntityMeta().getTableMeta().getFullName()
                + insertClause.toIntoSql()
                + insertClause.toValuesSql();

        this.executedSql = sql;
    }

    /**
     * 挿入の実行
     * @return 更新した行数
     */
    public int execute() {
        assertNotCompleted("execute");

        final int rows;
        if(this.usingIdentityGeneratedColumnNames.isEmpty()) {
            // 主キーがIDENTITYによる生成でない場合
            rows = context.getNamedParameterJdbcTemplate().update(executedSql, this.paramSource);

        } else {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            rows = context.getNamedParameterJdbcTemplate().update(executedSql, paramSource, keyHolder,
                    usingIdentityGeneratedColumnNames.toArray(new String[usingIdentityGeneratedColumnNames.size()]));

            // 生成した主キーをエンティティに設定する
            for(Map.Entry<String, Object> entry : keyHolder.getKeys().entrySet()) {

                if(!usingIdentityGeneratedColumnNames.contains(entry.getKey())) {
                    continue;
                }

                PropertyMeta propertyMeta = query.getEntityMeta().getColumnPropertyMeta(entry.getKey())
                            .orElseThrow();
                IdentityIdGenerator idGenerator = (IdentityIdGenerator) propertyMeta.getIdGenerator().get();
                Object propertyValue = idGenerator.generateValue((Number)entry.getValue());
                PropertyValueInvoker.setPropertyValue(propertyMeta, query.getEntity(), propertyValue);

            }
        }

        return rows;

    }
}

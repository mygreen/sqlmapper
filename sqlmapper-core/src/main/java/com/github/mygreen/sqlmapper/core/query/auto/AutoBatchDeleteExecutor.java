package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.core.query.QueryExecutorSupport;
import com.github.mygreen.sqlmapper.core.query.WhereClause;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.where.simple.SimpleWhereBuilder;
import com.github.mygreen.sqlmapper.core.where.simple.SimpleWhereVisitor;

/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class AutoBatchDeleteExecutor extends QueryExecutorSupport<AutoBatchDelete<?>> {

    /**
     * where句
     */
    private final WhereClause whereClause = new WhereClause();

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータです。
     */
    private final List<Object> paramValues = new ArrayList<>();

    public AutoBatchDeleteExecutor(AutoBatchDelete<?> query) {
        super(query);
    }

    @Override
    public void prepare() {

        prepareWhereClause();
        prepareSql();

        completed();

    }

    /**
     * 条件文の組み立てを行います
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareWhereClause() {

        final SimpleWhereBuilder where = new SimpleWhereBuilder();

        final int dataSize = query.getEntitySize();

        // 各レコードを１つの条件分でつなげる
        for(int i=0; i < dataSize; i++) {
            final Object entity = query.getEntity(i);

            // 主キーを条件分として組み立てます
            for(PropertyMeta propertyMeta : query.getEntityMeta().getIdPropertyMetaList()) {
                String exp = String.format("%s = ?", propertyMeta.getColumnMeta().getName());

                Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, entity);
                ValueType valueType = propertyMeta.getValueType();
                Object value = valueType.getSqlParameterValue(propertyValue);

                where.exp(exp, value);
            }

            // 楽観的排他チェックを行うときは、バージョンキーも条件に加えます。
            if(isOptimisticLock()) {
                final PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();
                String exp = String.format("%s = ?", propertyMeta.getColumnMeta().getName());

                Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, entity);
                ValueType valueType = propertyMeta.getValueType();
                Object value = valueType.getSqlParameterValue(propertyValue);

                where.exp(exp, value);
            }

            where.or();

        }

        SimpleWhereVisitor visitor = new SimpleWhereVisitor();
        where.accept(visitor);

        this.whereClause.addSql(visitor.getCriteria());
        this.paramValues.add(visitor.getParamValues());
    }

    /**
     * 実行するSQLを組み立てます。
     */
    public void prepareSql() {

        final String sql = "DELETE FROM "
                + query.getEntityMeta().getTableMeta().getFullName()
                + whereClause.toSql();

        this.executedSql = sql;
    }

    /**
     * 楽観的同時実行制御を行っている場合は<code>true</code>を返します。
     * @return 楽観的同時実行制御を行っている場合は<code>true</code>
     */
    private boolean isOptimisticLock() {
        return !query.isIgnoreVersion() && query.getEntityMeta().hasVersionPropertyMeta();
    }

    /**
     * 削除処理を実行します。
     * @return 削除したレコード件数を返します。
     */
    public int execute() {

        assertNotCompleted("executeBatchDelete");

        final int rows = context.getJdbcTemplate().update(executedSql, paramValues.toArray());
        if(isOptimisticLock()) {
            validateRows(rows);
        }
        return rows;


    }

    /**
     * 更新対象のレコード数のチェック
     * @param rows 更新したレコード数
     */
    private void validateRows(final int rows) {
        if(!query.isSuppresOptimisticLockException() && rows != query.getEntitySize()) {
            throw new OptimisticLockingFailureException(context.getMessageFormatter().create("query.alreadyBatchUpdate")
                    .format());
        }
    }
}

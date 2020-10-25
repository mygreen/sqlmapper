package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.core.query.QueryExecutorSupport;
import com.github.mygreen.sqlmapper.core.query.WhereClause;
import com.github.mygreen.sqlmapper.core.where.WhereBuilder;
import com.github.mygreen.sqlmapper.core.where.WhereVisitor;

/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class AutoDeleteExecutor extends QueryExecutorSupport<AutoDelete<?>> {

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

    public AutoDeleteExecutor(AutoDelete<?> query) {
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
    private void prepareWhereClause() {

        final WhereBuilder where = new WhereBuilder();

        // 主キーを条件分として組み立てます
        for(PropertyMeta propertyMeta : query.getEntityMeta().getIdPropertyMetaList()) {
            Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity());
            where.eq(propertyMeta.getName(), propertyValue);
        }

        // 楽観的排他チェックを行うときは、バージョンキーも条件に加えます。
        if(isOptimisticLock()) {
            final PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();
            Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity());
            where.eq(propertyMeta.getName(), propertyValue);
        }

        WhereVisitor visitor = new WhereVisitor(query.getEntityMeta());
        where.accept(visitor);

        this.whereClause.addSql(visitor.getCriteria());
        this.paramValues.addAll(visitor.getParamValues());
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

        assertNotCompleted("executeDelete");

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
        if(!query.isSuppresOptimisticLockException() && rows == 0) {
            throw new OptimisticLockingFailureException(context.getMessageFormatter().create("query.alreadyUpdate")
                    .param("entity", query.getEntity())
                    .format());
        }
    }
}

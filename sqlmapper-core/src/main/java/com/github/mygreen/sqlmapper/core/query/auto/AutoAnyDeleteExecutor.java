package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.mygreen.sqlmapper.core.query.QueryExecutorSupport;
import com.github.mygreen.sqlmapper.core.query.TableNameResolver;
import com.github.mygreen.sqlmapper.core.query.WhereClause;
import com.github.mygreen.sqlmapper.core.where.metamodel.MetamodelWhere;
import com.github.mygreen.sqlmapper.core.where.metamodel.MetamodelWhereVisitor;

public class AutoAnyDeleteExecutor extends QueryExecutorSupport<AutoAnyDelete<?>> {

    /**
     * where句
     */
    private final WhereClause whereClause = new WhereClause();

    /**
     * テーブルの別名を管理します。
     */
    private final TableNameResolver tableNameResolver = new TableNameResolver();

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータです。
     */
    private final List<Object> paramValues = new ArrayList<>();

    public AutoAnyDeleteExecutor(AutoAnyDelete<?> query) {
        super(query);
    }

    @Override
    public void prepare() {

        prepareCondition();
        prepareSql();

        completed();

    }

    /**
     * 条件文の組み立てを行います
     */
    private void prepareCondition() {

        if(query.getWhere() == null) {
            return;
        }

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(
                Map.of(query.getBaseClass(), query.getEntityMeta()),
                context.getDialect(),
                tableNameResolver);
        visitor.visit(new MetamodelWhere(query.getWhere()));

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
     * 削除処理を実行します。
     * @return 削除したレコード件数を返します。
     */
    public int execute() {

        assertNotCompleted("executeAnyDelete");

        try {
            return context.getJdbcTemplate().update(executedSql, paramValues.toArray());

        } finally {
            completed();
        }


    }

}

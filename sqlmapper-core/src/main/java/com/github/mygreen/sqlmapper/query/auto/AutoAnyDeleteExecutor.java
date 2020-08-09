package com.github.mygreen.sqlmapper.query.auto;

import java.util.ArrayList;
import java.util.List;

import com.github.mygreen.sqlmapper.query.QueryExecutorSupport;
import com.github.mygreen.sqlmapper.query.WhereClause;
import com.github.mygreen.sqlmapper.where.WhereVisitor;

public class AutoAnyDeleteExecutor extends QueryExecutorSupport<AutoAnyDelete<?>> {

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

        if(query.getCriteria() == null) {
            return;
        }

        WhereVisitor visitor = new WhereVisitor(query.getEntityMeta());
        query.getCriteria().accept(visitor);

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

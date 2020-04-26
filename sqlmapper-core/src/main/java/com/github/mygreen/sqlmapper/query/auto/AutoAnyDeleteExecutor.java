package com.github.mygreen.sqlmapper.query.auto;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.query.QueryExecutorBase;
import com.github.mygreen.sqlmapper.query.WhereClause;
import com.github.mygreen.sqlmapper.where.WhereVisitor;
import com.github.mygreen.sqlmapper.where.WhereVisitorParamContext;

public class AutoAnyDeleteExecutor extends QueryExecutorBase {

    private final AutoAnyDelete<?> query;

    /**
     * where句
     */
    private final WhereClause whereClause = new WhereClause();

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータ
     */
    private final MapSqlParameterSource paramSource = new MapSqlParameterSource();

    /**
     * クエリ条件のパラメータに関する情報
     */
    private final WhereVisitorParamContext paramContext = new WhereVisitorParamContext(paramSource);

    public AutoAnyDeleteExecutor(AutoAnyDelete<?> query) {
        super(query.getContext());
        this.query = query;
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

        WhereVisitor visitor = new WhereVisitor(query.getEntityMeta(), paramContext);
        query.getCriteria().accept(visitor);

        this.whereClause.addSql(visitor.getCriteria());
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

        assertNotCompleted("execute");

        final int rows = context.getNamedParameterJdbcTemplate().update(executedSql, paramSource);
        return rows;


    }

}

package com.github.mygreen.sqlmapper.query.sql;

import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.query.QueryBase;

public class SqlUpdate<T> extends QueryBase<T> {

    /**
     * SQLテンプレートです。
     */
    private SqlTemplate template;

    /**
     * SQLテンプレートのパラメータです。
     */
    private SqlTemplateContext parameter;

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータ
     */
    private Object[] paramValues;


    public SqlUpdate(SqlMapperContext context, SqlTemplate template, SqlTemplateContext parameter) {
        super(context);
        this.template = template;
        this.parameter = parameter;
    }

    private void prepare() {

        prepareSql();

    }

    private void prepareSql() {

        final ProcessResult result = template.process(parameter);
        this.executedSql = result.getSql();
        this.paramValues = result.getParameters().toArray();
    }

    /**
     * 更新クエリを実行します。
     * @return 更新したレコード件数を返します。
     */
    public int execute() {
        assertNotCompleted("executeSqlUpdate");

        prepare();

        try {
            int rows = context.getJdbcTemplate().update(executedSql, paramValues);
            return rows;
        } finally {
            completed();
        }

    }
}

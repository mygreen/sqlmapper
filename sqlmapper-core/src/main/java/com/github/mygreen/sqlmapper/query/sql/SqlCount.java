package com.github.mygreen.sqlmapper.query.sql;

import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.query.QueryBase;

public class SqlCount<T> extends QueryBase<T> {

    /**
     * SQLテンプレート
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


    public SqlCount(SqlMapperContext context, SqlTemplate template, SqlTemplateContext parameter) {
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
     * カウント用のクエリを実行します。
     * @return カウント結果
     */
    public long getCount() {
        assertNotCompleted("getCount");

        prepare();

        try {
            return context.getJdbcTemplate().queryForObject(executedSql, paramValues, Long.class);
        } finally {
            completed();
        }

    }
}

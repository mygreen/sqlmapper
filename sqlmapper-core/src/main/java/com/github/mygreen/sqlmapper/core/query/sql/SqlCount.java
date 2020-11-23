package com.github.mygreen.sqlmapper.core.query.sql;

import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;

/**
 * SQLテンプレートによる件数カウントです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlCount extends SqlTemplateQuerySupport<Void> {

    public SqlCount(SqlMapperContext context, SqlTemplate template, SqlTemplateContext parameter) {
        super(context, template, parameter);
    }

    /**
     * カウント用のクエリを実行します。
     * @return カウント結果
     */
    public long getCount() {
        ProcessResult result = template.process(parameter);
        return context.getJdbcTemplate().queryForObject(result.getSql(), Long.class, result.getParameters().toArray());

    }
}

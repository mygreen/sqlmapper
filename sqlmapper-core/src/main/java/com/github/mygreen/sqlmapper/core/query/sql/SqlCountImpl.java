package com.github.mygreen.sqlmapper.core.query.sql;

import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;

/**
 * SQLテンプレートによる件数のカウントを行うクエリの実装です。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlCountImpl extends SqlTemplateQuerySupport<Void> implements SqlCount {

    public SqlCountImpl(SqlMapperContext context, SqlTemplate template, SqlTemplateContext parameter) {
        super(context, template, parameter);
    }

    @Override
    public long getCount() {
        ProcessResult result = template.process(parameter);
        return context.getJdbcTemplate().queryForObject(result.getSql(), Long.class, result.getParameters().toArray());

    }
}

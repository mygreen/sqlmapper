package com.github.mygreen.sqlmapper.core.query.sql;

import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;


/**
 * SQLテンプレートによる更新（INSERT / UPDATE/ DELETE）です。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlUpdateImpl extends SqlTemplateQuerySupport<Void> implements SqlUpdate {

    public SqlUpdateImpl(SqlMapperContext context, SqlTemplate template, SqlTemplateContext parameter) {
        super(context, template, parameter);
    }

    @Override
    public int execute() {
        ProcessResult result = template.process(parameter);
        return context.getJdbcTemplate().update(result.getSql(), result.getParameters().toArray());

    }
}

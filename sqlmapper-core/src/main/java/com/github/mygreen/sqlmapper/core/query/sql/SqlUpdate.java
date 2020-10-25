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
public class SqlUpdate extends SqlTemplateQuerySupport<Void> {

    public SqlUpdate(SqlMapperContext context, SqlTemplate template, SqlTemplateContext parameter) {
        super(context, template, parameter);
    }

    /**
     * 更新クエリを実行します。
     * @return 更新したレコード件数を返します。
     */
    public int execute() {
        ProcessResult result = template.process(parameter);
        return context.getJdbcTemplate().update(result.getSql(), result.getParameters().toArray());

    }
}

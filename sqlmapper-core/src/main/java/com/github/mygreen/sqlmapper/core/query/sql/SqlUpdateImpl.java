package com.github.mygreen.sqlmapper.core.query.sql;

import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;

import lombok.Getter;


/**
 * SQLテンプレートによる更新（INSERT / UPDATE/ DELETE）を行うクエリの実装です。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlUpdateImpl implements SqlUpdate {

    /**
     * SqlMapperの設定情報。
     */
    @Getter
    private final SqlMapperContext context;

    /**
     * SQLテンプレートです。
     */
    @Getter
    private final SqlTemplate template;

    /**
     * SQLテンプレートのパラメータです。
     */
    @Getter
    private final SqlTemplateContext parameter;

    public SqlUpdateImpl(SqlMapperContext context, SqlTemplate template, SqlTemplateContext parameter) {
        this.context = context;
        this.template = template;
        this.parameter = parameter;
    }

    @Override
    public int execute() {
        ProcessResult result = template.process(parameter);
        return context.getJdbcTemplate().update(result.getSql(), result.getParameters().toArray());

    }
}

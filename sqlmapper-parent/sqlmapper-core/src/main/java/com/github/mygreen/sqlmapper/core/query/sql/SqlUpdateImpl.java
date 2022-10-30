package com.github.mygreen.sqlmapper.core.query.sql;

import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.query.JdbcTemplateBuilder;

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
    private final SqlTemplateContext<?> parameter;

    @Getter
    private Integer queryTimeout;

    public SqlUpdateImpl(SqlMapperContext context, SqlTemplate template, SqlTemplateContext<?> parameter) {
        this.context = context;
        this.template = template;
        this.parameter = parameter;
    }

    @Override
    public SqlUpdateImpl queryTimeout(int seconds) {
        this.queryTimeout = seconds;
        return this;
    }

    @Override
    public int execute() {
        ProcessResult result = template.process(parameter);
        return getJdbcTemplate().update(result.getSql(), result.getParameters().toArray());

    }

    /**
     * {@link JdbcTemplate}を取得します。
     * @return {@link JdbcTemplate}のインスタンス。
     */
    private JdbcTemplate getJdbcTemplate() {
        return JdbcTemplateBuilder.create(context.getDataSource(), context.getJdbcTemplateProperties())
                .queryTimeout(queryTimeout)
                .build();
    }

}

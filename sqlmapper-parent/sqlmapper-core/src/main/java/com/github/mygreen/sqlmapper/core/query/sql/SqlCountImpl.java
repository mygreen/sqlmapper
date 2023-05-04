package com.github.mygreen.sqlmapper.core.query.sql;

import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.query.JdbcTemplateBuilder;

import lombok.Getter;

/**
 * SQLテンプレートによる件数のカウントを行うクエリの実装です。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlCountImpl implements SqlCount {

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

    public SqlCountImpl(SqlMapperContext context, SqlTemplate template, SqlTemplateContext<?> parameter) {
        this.context = context;
        this.template = template;
        this.parameter = parameter;
    }

    @Override
    public SqlCountImpl queryTimeout(int seconds) {
        this.queryTimeout = seconds;
        return this;
    }

    @Override
    public long getCount() {
        ProcessResult result = template.process(parameter);
        String sql = context.getDialect().convertGetCountSql(result.getSql());

        context.getSqlLogger().out(sql, result.getParameters());

        return getJdbcTemplate().queryForObject(sql, Long.class, result.getParameters().toArray());

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

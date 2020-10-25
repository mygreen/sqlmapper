package com.github.mygreen.sqlmapper.core.query.sql;

import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.query.QuerySupport;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * SQLテンプレートによるクエリを組み立てる処理のサポートクラス。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public abstract class SqlTemplateQuerySupport<T> extends QuerySupport<T> {

    /**
     * SQLテンプレートです。
     */
    @Getter(AccessLevel.PROTECTED)
    protected final SqlTemplate template;

    /**
     * SQLテンプレートのパラメータです。
     */
    @Getter(AccessLevel.PROTECTED)
    protected final SqlTemplateContext parameter;

    public SqlTemplateQuerySupport(SqlMapperContext context, SqlTemplate template, SqlTemplateContext parameter) {
        super(context);
        this.template = template;
        this.parameter = parameter;
    }
}

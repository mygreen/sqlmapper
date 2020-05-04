package com.github.mygreen.sqlmapper.query.sql;

import org.springframework.core.io.Resource;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.query.QueryBase;

public class SqSelect<T> extends QueryBase<T> {

    private Resource resource;

    private Object parameter;

    public SqSelect(SqlMapperContext context, Resource resource, Object parameter) {
        super(context);
        this.resource = resource;
        this.parameter = parameter;
    }

    public SqSelect(SqlMapperContext context, Resource resource) {
        this(context, resource, null);
    }
}

package com.github.mygreen.sqlmapper.metamodel;

import java.sql.Date;

import com.github.mygreen.sqlmapper.metamodel.expression.SqlDateExpression;

public class SqlDatePath extends SqlDateExpression implements PropertyPath<Date> {

    private PathMixin<Date> pathMixin;

    public SqlDatePath(PathMixin<Date> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    public SqlDatePath(Path<?> parent, String propertyName) {
        this(new PathMixin<>(Date.class, PathMetaUtils.forProperty(parent, propertyName)));
    }

    @Override
    public PathMeta getPathMeta() {
        return pathMixin.getPathMeta();
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(pathMixin, context);
    }
}

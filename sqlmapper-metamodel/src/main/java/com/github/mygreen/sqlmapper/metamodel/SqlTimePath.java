package com.github.mygreen.sqlmapper.metamodel;

import java.sql.Time;

import com.github.mygreen.sqlmapper.metamodel.expression.SqlTimeExpression;

public class SqlTimePath extends SqlTimeExpression implements PropertyPath<Time> {

    private PathMixin<Time> pathMixin;

    public SqlTimePath(PathMixin<Time> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    public SqlTimePath(Path<?> parent, String propertyName) {
        this(new PathMixin<>(Time.class, PathMetaUtils.forProperty(parent, propertyName)));
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

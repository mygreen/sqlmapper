package com.github.mygreen.sqlmapper.metamodel;

import java.sql.Timestamp;

import com.github.mygreen.sqlmapper.metamodel.expression.SqlTimestampExpression;

public class SqlTimestampPath extends SqlTimestampExpression implements PropertyPath<Timestamp> {

    private PathMixin<Timestamp> pathMixin;

    public SqlTimestampPath(PathMixin<Timestamp> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    public SqlTimestampPath(Path<?> parent, String propertyName) {
        this(new PathMixin<>(Timestamp.class, PathMetaUtils.forProperty(parent, propertyName)));
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

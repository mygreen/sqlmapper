package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.NumberExpression;

public class NumberPath<T extends Number & Comparable<T>> extends NumberExpression<T> implements PropertyPath<T> {

    private final PathMixin<T> pathMixin;

    public NumberPath(PathMixin<T> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    public NumberPath(Class<? extends T> type, Path<?> parent, String propertyName) {
        this(new PathMixin<>(type, PathMetaUtils.forProperty(parent, propertyName)));
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

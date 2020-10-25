package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.GeneralExpression;

public class GeneralPath<T> extends GeneralExpression<T> implements PropertyPath<T> {

    private final PathMixin<T> pathMixin;

    public GeneralPath(PathMixin<T> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    public GeneralPath(Class<? extends T> type, Path<?> parent, String propertyName) {
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

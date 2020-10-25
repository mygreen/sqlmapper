package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.BooleanExpression;

public class BooleanPath extends BooleanExpression implements PropertyPath<Boolean> {

    private final PathMixin<Boolean> pathMixin;

    protected BooleanPath(PathMixin<Boolean> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    public BooleanPath(Path<?> parent, String propertyName) {
        this(new PathMixin<>(Boolean.class, PathMetaUtils.forProperty(parent, propertyName)));
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(this, context);
    }

    @Override
    public PathMeta getPathMeta() {
        return pathMixin.getPathMeta();
    }


}

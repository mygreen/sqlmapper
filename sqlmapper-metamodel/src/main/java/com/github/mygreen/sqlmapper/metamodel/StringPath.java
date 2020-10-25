package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.StringExpression;


public class StringPath extends StringExpression implements PropertyPath<String> {

    private final PathMixin<String> pathMixin;

    protected StringPath(PathMixin<String> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    public StringPath(Path<?> parent, String propertyName) {
        this(new PathMixin<>(String.class, PathMetaUtils.forProperty(parent, propertyName)));
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

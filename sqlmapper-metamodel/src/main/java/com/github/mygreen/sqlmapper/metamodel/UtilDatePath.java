package com.github.mygreen.sqlmapper.metamodel;

import java.util.Date;

import com.github.mygreen.sqlmapper.metamodel.expression.UtilDateExpression;

public class UtilDatePath extends UtilDateExpression implements PropertyPath<Date> {

    private PathMixin<Date> pathMixin;

    public UtilDatePath(PathMixin<Date> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    public UtilDatePath(Path<?> parent, String propertyName) {
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

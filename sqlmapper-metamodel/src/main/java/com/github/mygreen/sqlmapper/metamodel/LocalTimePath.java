package com.github.mygreen.sqlmapper.metamodel;

import java.time.LocalTime;

import com.github.mygreen.sqlmapper.metamodel.expression.LocalTimeExpression;

public class LocalTimePath extends LocalTimeExpression implements PropertyPath<LocalTime> {

    private PathMixin<LocalTime> pathMixin;

    public LocalTimePath(PathMixin<LocalTime> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    public LocalTimePath(Path<?> parent, String propertyName) {
        this(new PathMixin<>(LocalTime.class, PathMetaUtils.forProperty(parent, propertyName)));
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

package com.github.mygreen.sqlmapper.metamodel;

import java.time.LocalDate;

import com.github.mygreen.sqlmapper.metamodel.expression.LocalDateExpression;

public class LocalDatePath extends LocalDateExpression implements PropertyPath<LocalDate> {

    private PathMixin<LocalDate> pathMixin;

    public LocalDatePath(PathMixin<LocalDate> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    public LocalDatePath(Path<?> parent, String propertyName) {
        this(new PathMixin<>(LocalDate.class, PathMetaUtils.forProperty(parent, propertyName)));
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

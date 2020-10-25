package com.github.mygreen.sqlmapper.metamodel;

import java.time.LocalDateTime;

import com.github.mygreen.sqlmapper.metamodel.expression.LocalDateTimeExpression;

public class LocalDateTimePath extends LocalDateTimeExpression implements PropertyPath<LocalDateTime> {

    private PathMixin<LocalDateTime> pathMixin;

    public LocalDateTimePath(PathMixin<LocalDateTime> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    public LocalDateTimePath(Path<?> parent, String propertyName) {
        this(new PathMixin<>(LocalDateTime.class, PathMetaUtils.forProperty(parent, propertyName)));
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

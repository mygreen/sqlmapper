package com.github.mygreen.sqlmapper.metamodel;

import java.time.LocalDateTime;

import com.github.mygreen.sqlmapper.metamodel.expression.LocalDateTimeExpression;

/**
 * {@link LocalDateTime}型のプロパティを表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class LocalDateTimePath extends LocalDateTimeExpression implements PropertyPath<LocalDateTime> {

    private PathMixin<LocalDateTime> pathMixin;

    /**
     * プロパティの情報を指定してインスタンスを作成する。
     * @param mixin プロパティの情報
     */
    protected LocalDateTimePath(PathMixin<LocalDateTime> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    /**
     * プロパティが所属するエンティティの情報とプロパティ名を指定してインスタンスを作成する。
     * @param parent プロパティが属するエンティティのパス情報。
     * @param propertyName プロパティ名
     */
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

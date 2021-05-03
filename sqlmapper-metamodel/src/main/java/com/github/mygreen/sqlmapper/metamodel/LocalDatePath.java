package com.github.mygreen.sqlmapper.metamodel;

import java.time.LocalDate;

import com.github.mygreen.sqlmapper.metamodel.expression.LocalDateExpression;

/**
 * {@link LocalDate}型のプロパティを表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class LocalDatePath extends LocalDateExpression implements PropertyPath<LocalDate> {

    private PathMixin<LocalDate> pathMixin;

    /**
     * プロパティの情報を指定してインスタンスを作成する。
     * @param mixin プロパティの情報
     */
    protected LocalDatePath(PathMixin<LocalDate> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    /**
     * プロパティが所属するエンティティの情報とプロパティ名を指定してインスタンスを作成する。
     * @param parent プロパティが属するエンティティのパス情報。
     * @param propertyName プロパティ名
     */
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

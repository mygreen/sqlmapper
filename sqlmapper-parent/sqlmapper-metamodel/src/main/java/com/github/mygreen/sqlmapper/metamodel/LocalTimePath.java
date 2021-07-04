package com.github.mygreen.sqlmapper.metamodel;

import java.time.LocalTime;

import com.github.mygreen.sqlmapper.metamodel.expression.LocalTimeExpression;

/**
 * {@link LocalTime}型のプロパティを表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class LocalTimePath extends LocalTimeExpression implements PropertyPath<LocalTime> {

    private PathMixin<LocalTime> pathMixin;

    /**
     * プロパティの情報を指定してインスタンスを作成する。
     * @param mixin プロパティの情報
     */
    protected LocalTimePath(PathMixin<LocalTime> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    /**
     * プロパティが所属するエンティティの情報とプロパティ名を指定してインスタンスを作成する。
     * @param parent プロパティが属するエンティティのパス情報。
     * @param propertyName プロパティ名
     */
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

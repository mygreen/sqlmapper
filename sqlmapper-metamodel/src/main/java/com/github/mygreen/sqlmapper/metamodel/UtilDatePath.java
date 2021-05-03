package com.github.mygreen.sqlmapper.metamodel;

import java.util.Date;

import com.github.mygreen.sqlmapper.metamodel.expression.UtilDateExpression;

/**
 * {@link Date}型のプロパティを表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class UtilDatePath extends UtilDateExpression implements PropertyPath<Date> {

    private PathMixin<Date> pathMixin;

    /**
     * プロパティの情報を指定してインスタンスを作成する。
     * @param mixin プロパティの情報
     */
    public UtilDatePath(PathMixin<Date> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    /**
     * プロパティが所属するエンティティの情報とプロパティ名を指定してインスタンスを作成する。
     * @param parent プロパティが属するエンティティのパス情報。
     * @param propertyName プロパティ名
     */
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

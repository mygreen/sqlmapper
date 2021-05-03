package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.BooleanExpression;

/**
 * ブーリアン型のプロパティを表現します。
 *
 * @author T.TSUCHIE
 *
 */
public class BooleanPath extends BooleanExpression implements PropertyPath<Boolean> {

    private final PathMixin<Boolean> pathMixin;

    /**
     * プロパティの情報を指定してインスタンスを作成する。
     * @param mixin プロパティの情報
     */
    protected BooleanPath(PathMixin<Boolean> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    /**
     * プロパティが所属するエンティティの情報とプロパティ名を指定してインスタンスを作成する。
     * @param parent プロパティが属するエンティティのパス情報。
     * @param propertyName プロパティ名
     */
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

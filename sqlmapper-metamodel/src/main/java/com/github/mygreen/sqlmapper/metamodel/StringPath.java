package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.StringExpression;

/**
 * 文字列型のプロパティを表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class StringPath extends StringExpression implements PropertyPath<String> {

    private final PathMixin<String> pathMixin;

    /**
     * プロパティの情報を指定してインスタンスを作成する。
     * @param mixin プロパティの情報
     */
    protected StringPath(PathMixin<String> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    /**
     * プロパティが所属するエンティティの情報とプロパティ名を指定してインスタンスを作成する。
     * @param parent プロパティが属するエンティティのパス情報。
     * @param propertyName プロパティ名
     */
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

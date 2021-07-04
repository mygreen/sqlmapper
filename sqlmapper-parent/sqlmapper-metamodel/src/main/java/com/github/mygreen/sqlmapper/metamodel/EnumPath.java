package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.EnumExpression;


/**
 * 列挙型のプロパティを表現します。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 列挙型のタイプ
 */
public class EnumPath<T extends Enum<T>> extends EnumExpression<T> implements PropertyPath<T> {

    private final PathMixin<T> pathMixin;

    /**
     * プロパティの情報を指定してインスタンスを作成する。
     * @param mixin プロパティの情報
     */
    protected EnumPath(PathMixin<T> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    /**
     * プロパティが所属するエンティティの情報とプロパティ名を指定してインスタンスを作成する。
     * @param type 列挙型のクラスタイプ
     * @param parent プロパティが属するエンティティのパス情報。
     * @param propertyName プロパティ名
     */
    public EnumPath(Class<? extends T> type, Path<?> parent, String propertyName) {
        this(new PathMixin<>(type, PathMetaUtils.forProperty(parent, propertyName)));
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

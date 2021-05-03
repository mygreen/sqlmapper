package com.github.mygreen.sqlmapper.metamodel;

import java.sql.Timestamp;

import com.github.mygreen.sqlmapper.metamodel.expression.SqlTimestampExpression;


/**
 * {@link Timestamp} 型のプロパティを表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlTimestampPath extends SqlTimestampExpression implements PropertyPath<Timestamp> {

    private PathMixin<Timestamp> pathMixin;

    /**
     * プロパティの情報を指定してインスタンスを作成する。
     * @param mixin プロパティの情報
     */
    protected SqlTimestampPath(PathMixin<Timestamp> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    /**
     * プロパティが所属するエンティティの情報とプロパティ名を指定してインスタンスを作成する。
     * @param parent プロパティが属するエンティティのパス情報。
     * @param propertyName プロパティ名
     */
    public SqlTimestampPath(Path<?> parent, String propertyName) {
        this(new PathMixin<>(Timestamp.class, PathMetaUtils.forProperty(parent, propertyName)));
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

package com.github.mygreen.sqlmapper.metamodel;

import java.sql.Date;
import java.sql.Time;

import com.github.mygreen.sqlmapper.metamodel.expression.SqlTimeExpression;

/**
 * {@link Date} 型のプロパティを表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlTimePath extends SqlTimeExpression implements PropertyPath<Time> {

    private PathMixin<Time> pathMixin;

    /**
     * プロパティの情報を指定してインスタンスを作成する。
     * @param mixin プロパティの情報
     */
    protected SqlTimePath(PathMixin<Time> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    /**
     * プロパティが所属するエンティティの情報とプロパティ名を指定してインスタンスを作成する。
     * @param parent プロパティが属するエンティティのパス情報。
     * @param propertyName プロパティ名
     */
    public SqlTimePath(Path<?> parent, String propertyName) {
        this(new PathMixin<>(Time.class, PathMetaUtils.forProperty(parent, propertyName)));
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

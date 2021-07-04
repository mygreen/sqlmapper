package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.ImmutableExpression;
import com.github.mygreen.sqlmapper.metamodel.support.OperationUtils;

import lombok.Getter;


/**
 * {@link Path}のMixin用の実装。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> クラスタイプ
 */
public class PathMixin<T> extends ImmutableExpression<T> implements Path<T> {

    /**
     * パスのメタ情報
     */
    @Getter
    private final PathMeta pathMeta;

    public PathMixin(Class<? extends T> type, PathMeta pathMeta) {
        super(type);
        this.pathMeta = pathMeta;
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(this, context);
    }

    /**
     * {@inheritDoc}
     * @return 式ノードを評価結果。
     */
    @Override
    public String toString() {
        return OperationUtils.toDebugString(this);
    }

}

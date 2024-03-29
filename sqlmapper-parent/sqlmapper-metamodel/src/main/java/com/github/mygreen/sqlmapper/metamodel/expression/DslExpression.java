package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.support.OperationUtils;

/**
 * DSL式のベースクラス。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 式のタイプ
 */
public abstract class DslExpression<T> implements Expression<T> {

    /**
     * 左辺や式の操作対象となるインスタンス
     */
    protected final Expression<T> mixin;

    public DslExpression(final Expression<T> mixin) {
        this.mixin = mixin;
    }

    @Override
    public Class<? extends T> getType() {
        return mixin.getType();
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

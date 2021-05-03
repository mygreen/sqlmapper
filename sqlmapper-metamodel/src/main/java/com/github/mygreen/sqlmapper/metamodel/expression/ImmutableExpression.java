package com.github.mygreen.sqlmapper.metamodel.expression;

import lombok.Getter;

/**
 * 不変な式の実装。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 式のクラスタイプ
 */
public abstract class ImmutableExpression<T> implements Expression<T> {

    /**
     * クラスタイプ
     */
    @Getter
    private final Class<? extends T> type;

    public ImmutableExpression(Class<? extends T> type) {
        this.type = type;
    }

}

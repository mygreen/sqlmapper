package com.github.mygreen.sqlmapper.metamodel.expression;

import lombok.Getter;

/**
 * イミュータブルな式の実装。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 式のタイプ
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

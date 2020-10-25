package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.Visitor;

public interface Expression<T> {

    /**
     *
     * @return 式の型タイプ
     */
    Class<? extends T> getType();

    /**
     *
     * @param <C> コンテキストのタイプ
     * @param visitor  ビジター
     * @param context コンテキスト
     */
    <C> void accept(Visitor<C> visitor, C context);
}

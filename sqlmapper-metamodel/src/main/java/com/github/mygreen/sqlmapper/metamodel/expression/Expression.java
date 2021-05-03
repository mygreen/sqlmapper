package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.Visitor;

/**
 * 一般的な式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 式が扱うクラスタイプ
 */
public interface Expression<T> {

    /**
     * 式のクラスタイプを取得します。
     * @return 式のクラスタイプ
     */
    Class<? extends T> getType();

    /**
     * 式であるノードを巡回するための{@link Visitor} を受け付けます。
     * @param <C> コンテキストのタイプ
     * @param visitor ビジター
     * @param context コンテキスト
     */
    <C> void accept(Visitor<C> visitor, C context);
}

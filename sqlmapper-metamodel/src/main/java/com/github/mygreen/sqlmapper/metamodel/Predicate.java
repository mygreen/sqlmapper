package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.Expression;

/**
 * 評価可能な式の終端を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface Predicate extends Expression<Boolean> {

    /**
     * 論理式の結果を反転します。
     * @return 論理式の結果を反転した結果を返します。
     */
    Predicate not();

    /**
     *
     * @param <C> コンテキストのタイプ
     * @param visitor  ビジター
     * @param context コンテキスト
     */
    <C> void accept(Visitor<C> visitor, C context);
}

package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.Expression;

/**
 * 評価可能な式の終端を表現します。
 * ブーリアン型の式の用インタフェースとして用います。
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface Predicate extends Expression<Boolean> {

    /**
     * 式の結果を否定する式を作成します。
     * @return {@literal NOT 左辺}
     */
    Predicate not();

    /**
     * {@inheritDoc}
     */
    <C> void accept(Visitor<C> visitor, C context);
}

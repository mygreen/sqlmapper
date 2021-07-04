package com.github.mygreen.sqlmapper.metamodel.expression;

import java.util.Date;

/**
 * {@link java.util.Date} を親に持つ日時型のベースクラス。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 日時型のクラスタイプ
 */
public abstract class DateExpressionBase<T extends Date & Comparable<Date>> extends ComparableExpression<T> {

    public DateExpressionBase(Expression<T> mixin) {
        super(mixin);
    }

    /**
     * {@literal 左辺 > 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 > 右辺}
     */
    public BooleanExpression after(T right) {
        return gt(right);
    }

    /**
     * {@literal 左辺 > 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 > 右辺}
     */
    public BooleanExpression after(Expression<T> right) {
        return gt(right);
    }

    /**
     * {@literal 左辺 < 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 < 右辺}
     */
    public BooleanExpression before(T right) {
        return lt(right);
    }

    /**
     * {@literal 左辺 < 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 < 右辺}
     */
    public BooleanExpression before(Expression<T> right) {
        return lt(right);
    }
}

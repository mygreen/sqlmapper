package com.github.mygreen.sqlmapper.metamodel.expression;

import java.util.Date;

/**
 * {@link java.util.Date} を親に持つ日時型のベースクラス。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T>
 */
public abstract class DateExpressionBase<T extends Date & Comparable<Date>> extends ComparableExpression<T> {

    public DateExpressionBase(Expression<T> mixin) {
        super(mixin);
    }

    /**
     * {@literal this > right}
     * @param right
     * @return
     */
    public BooleanExpression after(T right) {
        return gt(right);
    }

    /**
     * {@literal this > right}
     * @param right
     * @return
     */
    public BooleanExpression after(Expression<T> right) {
        return gt(right);
    }

    /**
     * {@literal this < right}
     * @param right
     * @return
     */
    public BooleanExpression before(T right) {
        return lt(right);
    }

    /**
     * {@literal this < right}
     * @param right
     * @return
     */
    public BooleanExpression before(Expression<T> right) {
        return lt(right);
    }
}

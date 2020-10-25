package com.github.mygreen.sqlmapper.metamodel.expression;

import java.time.temporal.TemporalAccessor;

/**
 * {@link TemporalAccessor} を日時型のベースクラス。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public abstract class TemporalExpressionBase<T extends TemporalAccessor & Comparable> extends ComparableExpression<T> {

    public TemporalExpressionBase(Expression<T> mixin) {
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

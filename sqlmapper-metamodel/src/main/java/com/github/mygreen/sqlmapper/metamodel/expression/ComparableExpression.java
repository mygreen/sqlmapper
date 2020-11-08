package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.operation.BooleanOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.ComparisionOp;

/**
 * 比較可能な型に対する式
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 比較可能なクラスタイプ。
 */
@SuppressWarnings("rawtypes")
public abstract class ComparableExpression<T extends Comparable> extends GeneralExpression<T> {

    public ComparableExpression(Expression<T> mixin) {
        super(mixin);
    }

    public BooleanExpression between(T from, T to) {
        if(from == null) {
            if(to != null) {
                return loe(to);
            }
            throw new IllegalArgumentException("Either from or to needs to be non-null.");

        } else if(to == null) {
            return goe(from);

        } else {
            return new BooleanOperation(ComparisionOp.BETWEEN, mixin, Constant.create(from), Constant.create(to));
        }
    }

    public BooleanExpression between(Expression<T> from, Expression<T> to) {
        if(from == null) {
            if(to != null) {
                return loe(to);
            }
            throw new IllegalArgumentException("Either from or to needs to be non-null.");

        } else if(to == null) {
            return goe(from);

        } else {
            return new BooleanOperation(ComparisionOp.BETWEEN, mixin, Constant.create(from), Constant.create(to));
        }
    }

    /**
     * {@literal this >= right}
     * @param right
     * @return
     */
    public BooleanExpression goe(T right) {
        return goe(Constant.create(right));
    }

    /**
     * {@literal this >= right}
     *
     * @param right
     * @return
     */
    public BooleanExpression goe(Expression<T> right) {
        return new BooleanOperation(ComparisionOp.GOE, mixin, right);
    }

    /**
     * {@literal this > right}
     *
     * @param right
     * @return
     */
    public BooleanExpression gt(T right) {
        return gt(Constant.create(right));
    }

    /**
     * {@literal this > right}
     *
     * @param right
     * @return
     */
    public BooleanExpression gt(Expression<T> right) {
        return new BooleanOperation(ComparisionOp.GT, mixin, right);
    }

    /**
     * {@literal this <= right}
     *
     * @param right
     * @return
     */
    public BooleanExpression loe(T right) {
        return loe(Constant.create(right));
    }

    /**
     * {@literal this <= right}
     *
     * @param right
     * @return
     */
    public BooleanExpression loe(Expression<T> right) {
        return new BooleanOperation(ComparisionOp.LOE, mixin, right);
    }

    /**
     * {@literal this < right}
     *
     * @param right
     * @return
     */
    public BooleanExpression lt(T right) {
        return lt(Constant.create(right));
    }

    /**
     * {@literal this < right}
     *
     * @param right
     * @return
     */
    public BooleanExpression lt(Expression<T> right) {
        return new BooleanOperation(ComparisionOp.LT, mixin, right);
    }

}

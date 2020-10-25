package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.operation.NumberOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.BinaryOp;

public abstract class NumberExpression<T extends Number & Comparable<T>> extends ComparableExpression<T> {

    public NumberExpression(Expression<T> mixin) {
        super(mixin);
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> add(N right) {
        return new NumberOperation<T>(getType(), BinaryOp.ADD, Constant.create(right));
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> add(Expression<N> right) {
        return new NumberOperation<T>(getType(), BinaryOp.ADD, right);
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> substract(N right) {
        return new NumberOperation<T>(getType(), BinaryOp.SUB, Constant.create(right));
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> substract(Expression<N> right) {
        return new NumberOperation<T>(getType(), BinaryOp.SUB, right);
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> multiply(N right) {
        return new NumberOperation<T>(getType(), BinaryOp.MULT, Constant.create(right));
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> multiply(Expression<N> right) {
        return new NumberOperation<T>(getType(), BinaryOp.MULT, right);
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> divide(N right) {
        return new NumberOperation<T>(getType(), BinaryOp.DIV, Constant.create(right));
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> divide(Expression<N> right) {
        return new NumberOperation<T>(getType(), BinaryOp.DIV, right);
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> mod(N right) {
        return new NumberOperation<T>(getType(), BinaryOp.MOD, Constant.create(right));
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> mod(Expression<N> right) {
        return new NumberOperation<T>(getType(), BinaryOp.MOD, right);
    }

}

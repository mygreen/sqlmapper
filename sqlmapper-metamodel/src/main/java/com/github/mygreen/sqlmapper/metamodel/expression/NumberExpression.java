package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.operation.NumberOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.ArithmeticOp;

public abstract class NumberExpression<T extends Number & Comparable<T>> extends ComparableExpression<T> {

    public NumberExpression(Expression<T> mixin) {
        super(mixin);
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> add(N right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.ADD, Constant.create(right));
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> add(Expression<N> right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.ADD, right);
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> substract(N right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.SUB, Constant.create(right));
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> substract(Expression<N> right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.SUB, right);
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> multiply(N right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.MULT, Constant.create(right));
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> multiply(Expression<N> right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.MULT, right);
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> divide(N right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.DIV, Constant.create(right));
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> divide(Expression<N> right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.DIV, right);
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> mod(N right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.MOD, Constant.create(right));
    }

    public <N extends Number & Comparable<?>> NumberExpression<T> mod(Expression<N> right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.MOD, right);
    }

}

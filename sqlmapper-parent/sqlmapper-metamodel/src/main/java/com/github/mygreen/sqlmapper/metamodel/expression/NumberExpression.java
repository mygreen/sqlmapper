package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.operation.NumberOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.ArithmeticOp;

/**
 * 数値型の式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 数値型のクラスタイプ
 */
public abstract class NumberExpression<T extends Number & Comparable<T>> extends ComparableExpression<T> {

    public NumberExpression(Expression<T> mixin) {
        super(mixin);
    }

    /**
     * {@literal 左辺 + 右辺} として比較する式を作成します。
     * @param <N> 数値型のクラスタイプ
     * @param right 右辺
     * @return {@literal 左辺 + 右辺}
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> add(N right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.ADD, mixin, Constant.create(right));
    }

    /**
     * {@literal 左辺 + 右辺} として比較する式を作成します。
     * @param <N> 数値型のクラスタイプ
     * @param right 右辺
     * @return {@literal 左辺 + 右辺}
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> add(Expression<N> right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.ADD, mixin, right);
    }

    /**
     * {@literal 左辺 - 右辺} として比較する式を作成します。
     * @param <N> 数値型のクラスタイプ
     * @param right 右辺
     * @return {@literal 左辺 - 右辺}
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> substract(N right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.SUB, mixin, Constant.create(right));
    }

    /**
     * {@literal 左辺 - 右辺} として比較する式を作成します。
     * @param <N> 数値型のクラスタイプ
     * @param right 右辺
     * @return {@literal 左辺 - 右辺}
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> substract(Expression<N> right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.SUB, mixin, right);
    }

    /**
     * {@literal 左辺 * 右辺} として比較する式を作成します。
     * @param <N> 数値型のクラスタイプ
     * @param right 右辺
     * @return {@literal 左辺 * 右辺}
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> multiply(N right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.MULT, mixin, Constant.create(right));
    }

    /**
     * {@literal 左辺 * 右辺} として比較する式を作成します。
     * @param <N> 数値型のクラスタイプ
     * @param right 右辺
     * @return {@literal 左辺 * 右辺}
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> multiply(Expression<N> right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.MULT, mixin, right);
    }

    /**
     * {@literal 左辺 / 右辺} として比較する式を作成します。
     * @param <N> 数値型のクラスタイプ
     * @param right 右辺
     * @return {@literal 左辺 / 右辺}
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> divide(N right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.DIV, mixin, Constant.create(right));
    }

    /**
     * {@literal 左辺 / 右辺} として比較する式を作成します。
     * @param <N> 数値型のクラスタイプ
     * @param right 右辺
     * @return {@literal 左辺 / 右辺}
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> divide(Expression<N> right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.DIV, mixin, right);
    }

    /**
     * {@literal 左辺 % 右辺} として比較する式を作成します。
     * @param <N> 数値型のクラスタイプ
     * @param right 右辺
     * @return {@literal 左辺 % 右辺}
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> mod(N right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.MOD, mixin, Constant.create(right));
    }

    /**
     * {@literal 左辺 % 右辺} として比較する式を作成します。
     * @param <N> 数値型のクラスタイプ
     * @param right 右辺
     * @return {@literal 左辺 % 右辺}
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> mod(Expression<N> right) {
        return new NumberOperation<T>(getType(), ArithmeticOp.MOD, mixin, right);
    }

}

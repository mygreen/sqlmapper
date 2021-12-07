package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.operation.BooleanOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.ComparisionOp;

/**
 * 比較可能な型に対する式を表現するためのベースクラス。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 比較可能な式のクラスタイプ。
 */
@SuppressWarnings("rawtypes")
public abstract class ComparableExpression<T extends Comparable> extends GeneralExpression<T> {

    public ComparableExpression(Expression<T> mixin) {
        super(mixin);
    }

    /**
     * {@literal 左辺 BETEEN <FROM値> AND <TO値>} として比較する式を作成します。
     * @param from FROM値
     * @param to TO値
     * @return {@literal 左辺 BETEEN <FROM値> AND <TO値>}
     */
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

    /**
     * {@literal 左辺 BETEEN <FROM式> AND <TO式>} として比較する式を作成します。
     * @param from FROM式
     * @param to TO式
     * @return {@literal 左辺 BETEEN <FROM式> AND <TO式>}
     */
    public BooleanExpression between(Expression<T> from, Expression<T> to) {
        if(from == null) {
            if(to != null) {
                return loe(to);
            }
            throw new IllegalArgumentException("Either from or to needs to be non-null.");

        } else if(to == null) {
            return goe(from);

        } else {
            return new BooleanOperation(ComparisionOp.BETWEEN, mixin, from, to);
        }
    }

    /**
     * {@literal 左辺 >= 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 >= 右辺}
     */
    public BooleanExpression goe(T right) {
        return goe(Constant.create(right));
    }

    /**
     * {@literal 左辺 >= 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 >= 右辺}
     */
    public BooleanExpression goe(Expression<T> right) {
        return new BooleanOperation(ComparisionOp.GOE, mixin, right);
    }

    /**
     * {@literal 左辺 > 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 > 右辺}
     */
    public BooleanExpression gt(T right) {
        return gt(Constant.create(right));
    }

    /**
     * {@literal 左辺 > 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 > 右辺}
     */
    public BooleanExpression gt(Expression<T> right) {
        return new BooleanOperation(ComparisionOp.GT, mixin, right);
    }

    /**
     * {@literal 左辺 <= 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 <= 右辺}
     */
    public BooleanExpression loe(T right) {
        return loe(Constant.create(right));
    }

    /**
     * {@literal 左辺 <= 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 <= 右辺}
     */
    public BooleanExpression loe(Expression<T> right) {
        return new BooleanOperation(ComparisionOp.LOE, mixin, right);
    }

    /**
     * {@literal 左辺 < 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 < 右辺}
     */
    public BooleanExpression lt(T right) {
        return lt(Constant.create(right));
    }

    /**
     * {@literal 左辺 < 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 < 右辺}
     */
    public BooleanExpression lt(Expression<T> right) {
        return new BooleanOperation(ComparisionOp.LT, mixin, right);
    }

}

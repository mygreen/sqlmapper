package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.operation.BooleanOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.BinaryOp;
import com.github.mygreen.sqlmapper.metamodel.operator.UnaryOp;

public abstract class BooleanExpression extends ComparableExpression<Boolean> implements Predicate {

    public BooleanExpression(Expression<Boolean> mixin) {
        super(mixin);
    }

    public BooleanExpression isTrue() {
        return eq(Constant.create(true));
    }

    public BooleanExpression isFalse() {
        return eq(Constant.create(false));
    }

    /**
     * 式の結果を否定します
     */
    public BooleanExpression not() {
        return new BooleanOperation(UnaryOp.NOT, mixin);
    }

    /**
     * 右辺を論理演算子 {@literal AND} で評価します。
     * @param right 右辺
     * @return
     */
    public BooleanExpression and(Predicate right) {
        if(right == null) {
            // 右辺がnullの場合何もしない
            return this;
        }
        return new BooleanOperation(BinaryOp.AND, mixin, right);
    }

    /**
     * 右辺を論理演算子 {@literal OR} で評価します。
     * @param right
     * @return
     */
    public BooleanExpression or(Predicate right) {
        if(right == null) {
            // 右辺がnullの場合何もしない
            return this;
        }
        return new BooleanOperation(BinaryOp.OR, mixin, right);
    }

    /**
     * 引数で指定した全ての和({@literal OR})に対して積({@literal AND})を取ります。
     * <p>例：{@literal 左辺 AND (A OR B OR C ...)}
     * @param predicates
     * @return
     */
    public BooleanExpression andAnyOf(Predicate... predicates) {
        if(predicates == null || predicates.length == 0) {
            // 右辺がnullの場合何もしない
            return this;

        }

        // 引数の述語をORで繋げる
        Predicate right = null;
        for(Predicate b : predicates) {
            if(b != null) {
                right = (right == null) ? b : new BooleanOperation(BinaryOp.OR, right, b);
            }
        }

        return and(right);
    }

    /**
     * 引数で指定した全ての積({@literal AND})に対して和({@literal OR})を取ります。
     * <p>例：{@literal 左辺 OR (A AND B AND C ...)}
     * @param predicates
     * @return
     */
    public BooleanExpression orAllOf(Predicate... predicates) {
        if(predicates == null || predicates.length == 0) {
            // 右辺がnullの場合何もしない
            return this;
        }

        // 引数の述語をANDで繋げる
        Predicate right = null;
        for(Predicate b : predicates) {
            if(b != null) {
                right = (right == null) ? b : new BooleanOperation(BinaryOp.AND, right, b);
            }
        }

        return or(right);
    }

}

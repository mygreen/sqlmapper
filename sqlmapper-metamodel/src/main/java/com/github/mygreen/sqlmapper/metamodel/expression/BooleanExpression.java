package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.operation.BooleanOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.BooleanOp;
import com.github.mygreen.sqlmapper.metamodel.operator.UnaryOp;

/**
 * ブーリアンによる式を表現します。
 *
 * @author T.TSUCHIE
 *
 */
public abstract class BooleanExpression extends ComparableExpression<Boolean> implements Predicate {

    public BooleanExpression(Expression<Boolean> mixin) {
        super(mixin);
    }

    /**
     * {@literal 左辺 == true} として比較する式を作成します。
     * @return {@literal 左辺 = TRUE}
     */
    public BooleanExpression isTrue() {
        return eq(Constant.createBoolean(true));
    }

    /**
     * {@literal 左辺 == false} として比較する式を作成します。
     * @return {@literal 左辺 = FALSE}
     */
    public BooleanExpression isFalse() {
        return eq(Constant.createBoolean(false));
    }

    @Override
    public BooleanExpression not() {
        return new BooleanOperation(UnaryOp.NOT, mixin);
    }

    /**
     * 右辺を論理席({@literal 左辺 AND 右辺})で評価します。
     * @param right 右辺
     * @return {@literal 左辺 AND 右辺}
     */
    public BooleanExpression and(final Predicate right) {
        if(right == null) {
            // 右辺がnullの場合何もしない
            return this;
        }
        return new BooleanOperation(BooleanOp.AND, mixin, right);
    }

    /**
     * 右辺を論理和( {@literal 左辺 OR 右辺}) で評価します。
     * @param right 右辺
     * @return {@literal 左辺 OR 右辺}
     */
    public BooleanExpression or(final Predicate right) {
        if(right == null) {
            // 右辺がnullの場合何もしない
            return this;
        }
        return new BooleanOperation(BooleanOp.OR, mixin, right);
    }

    /**
     * 引数で指定した全ての和({@literal OR})に対して積({@literal AND})を取ります。
     * <p>例：{@literal 左辺 AND (A OR B OR C ...)}
     *
     * @param predicates 和(OR)を取る対象の式
     * @return {@literal 左辺 AND (右辺1 OR 右辺2 OR 右辺3 ...)}
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
                right = (right == null) ? b : new BooleanOperation(BooleanOp.OR, right, b);
            }
        }

        return and(right);
    }

    /**
     * 引数で指定した全ての積({@literal AND})に対して和({@literal OR})を取ります。
     * <p>例：{@literal 左辺 OR (A AND B AND C ...)}
     *
     * @param predicates 和(AND)を取る対象の式
     * @return {@literal 左辺 OR (右辺1 AND 右辺2 AND 右辺3 ...)}
     */
    public BooleanExpression orAllOf(final Predicate... predicates) {
        if(predicates == null || predicates.length == 0) {
            // 右辺がnullの場合何もしない
            return this;
        }

        // 引数の述語をANDで繋げる
        Predicate right = null;
        for(Predicate b : predicates) {
            if(b != null) {
                right = (right == null) ? b : new BooleanOperation(BooleanOp.AND, right, b);
            }
        }

        return or(right);
    }

}

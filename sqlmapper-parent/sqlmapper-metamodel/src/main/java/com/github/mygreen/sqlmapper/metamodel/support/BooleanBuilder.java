package com.github.mygreen.sqlmapper.metamodel.support;

import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.operation.BooleanOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.PredicateOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.BooleanOp;

import lombok.Getter;

/**
 * {@link Predicate} 式を組み立てるためのヘルパークラス。
 *
 *
 * <pre class="highlight"><code class="java">
 * QEmployee employee = QEmployee.employee;
 * BooleanBuilder builder = new BooleanBuilder();
 * for (String name : names) {
 *     builder.or(employee.name.eq(name));
 * }
 * </code></pre>
 *
 *
 * @author T.TSUCHIE
 *
 */
public class BooleanBuilder implements Predicate {

    /**
     * 現在の式
     */
    @Getter
    private Predicate predicate;

    /**
     * {@link BooleanBuilder} のインスタンスを作成します。
     */
    public BooleanBuilder() {

    }

    /**
     * 初期式を指定して、{@link BooleanBuilder} のインスタンスを作成します。
     * @param initial 諸域式
     */
    public BooleanBuilder(Predicate initial) {
        this.predicate = initial;
    }

    @Override
    public Class<? extends Boolean> getType() {
        return Boolean.class;
    }

    /**
     * {@inheritDoc}
     * <p>評価する式がない場合は何もしません。
     */
    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        if(predicate != null) {
            predicate.accept(visitor, context);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return 否定する式がない場合は {@literal null} を返す。
     */
    @Override
    public Predicate not() {

        if(predicate != null) {
            return predicate.not();
        }

        return null;

    }

    /**
     * 式を持つかどうか判定します。
     * @return 式を持つとき、{@literal true}を返します。
     */
    public boolean hasValue() {
        return predicate != null;
    }

    /**
     * 右辺を論理積( {@literal 左辺 AND 右辺})で評価します。
     * <p>左辺が存在しない場合は何もしません。
     *
     * @param right 右辺。nullの場合、何もしません。
     * @return 自身のインスタンス
     */
    public BooleanBuilder and(Predicate right) {

        if(right != null) {
            if(predicate == null) {
                this.predicate = right;
            } else {
                predicate = new PredicateOperation(BooleanOp.AND, predicate, right);
            }
        }

        return this;

    }

    /**
     * 右辺を論理和( {@literal 左辺 OR 右辺})で評価します。
     * <p>左辺が存在しない場合は何もしません。
     *
     * @param right 右辺。nullの場合、何もしません。
     * @return 自身のインスタンス
     */
    public BooleanBuilder or(Predicate right) {

        if(right != null) {
            if(predicate == null) {
                this.predicate = right;
            } else {
                predicate = new PredicateOperation(BooleanOp.OR, predicate, right);
            }
        }

        return this;

    }

    /**
     * 引数で指定した全ての和({@literal OR})に対して積({@literal AND})を取ります。
     * <p>例：{@literal 左辺 AND (A OR B OR C ...)}
     *
     * @param predicates 和(OR)を取る対象の式
     * @return {@literal 左辺 AND (右辺1 OR 右辺2 OR 右辺3 ...)}
     */
    public BooleanBuilder andAnyOf(Predicate... predicates) {

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
    public BooleanBuilder orAllOf(Predicate... predicates) {

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

package com.github.mygreen.sqlmapper.metamodel.operation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;
import com.github.mygreen.sqlmapper.metamodel.operator.UnaryOp;
import com.github.mygreen.sqlmapper.metamodel.support.DebugVisitor;
import com.github.mygreen.sqlmapper.metamodel.support.DebugVisitorContext;

import lombok.NonNull;

/**
 * {@link Predicate} に対する 演算子処理の実装。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class PredicateOperation implements Operation<Boolean>, Predicate {

    /**
     * 演算子
     */
    private final Operator operator;

    /**
     * 演算子の引数(右辺や左辺など)
     */
    private final List<Expression<?>> args;

    public PredicateOperation(@NonNull Operator operator, Expression<?>... args) {
        this.operator = operator;
        this.args = Collections.unmodifiableList(Arrays.asList(args));
    }

    @Override
    public Class<? extends Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public Predicate not() {
        return new PredicateOperation(UnaryOp.NOT, this);
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public Expression<?> getArg(int index) {
        return args.get(index);
    }

    @Override
    public List<Expression<?>> getArgs() {
        return args;
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit((Operation<?>)this, context);
    }

    /**
     * {@inheritDoc}
     * @return 式ノードを評価結果。
     */
    @Override
    public String toString() {
        DebugVisitor visitor = new DebugVisitor();
        DebugVisitorContext context = new DebugVisitorContext();
        accept(visitor, context);

        return context.getCriteria();
    }
}

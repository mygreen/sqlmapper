package com.github.mygreen.sqlmapper.metamodel.operation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.expression.ImmutableExpression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;
import com.github.mygreen.sqlmapper.metamodel.support.OperationUtils;

/**
 * {@link Operation} のMixin用の実装。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> タイプ
 */
public class OperationMixin<T> extends ImmutableExpression<T> implements Operation<T> {

    private final Operator operator;

    private final List<Expression<?>> args;

    public OperationMixin(Class<? extends T> type, Operator operator, Expression<?>... args) {
        super(type);
        this.operator = operator;
        this.args = Collections.unmodifiableList(Arrays.asList(args));
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
    public Optional<Expression<?>> getOptArg(int index) {
        if(index >= args.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(args.get(index));
    }

    @Override
    public List<Expression<?>> getArgs() {
        return args;
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(this, context);
    }

    /**
     * {@inheritDoc}
     * @return 式ノードを評価結果。
     */
    @Override
    public String toString() {
        return OperationUtils.toDebugString(this);
    }
}

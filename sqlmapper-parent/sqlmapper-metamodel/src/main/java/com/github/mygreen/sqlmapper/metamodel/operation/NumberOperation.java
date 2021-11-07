package com.github.mygreen.sqlmapper.metamodel.operation;

import java.util.List;
import java.util.Optional;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.expression.NumberExpression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

import lombok.NonNull;


/**
 * 数値型を値に持つ式の演算操作を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class NumberOperation<T extends Number & Comparable<T>> extends NumberExpression<T> implements Operation<T> {

    private final OperationMixin<T> opMixin;

    protected NumberOperation(OperationMixin<T> mixin) {
        super(mixin);
        this.opMixin = mixin;
    }

    public NumberOperation(Class<? extends T> type, @NonNull Operator op, Expression<?>... args) {
        this(new OperationMixin<>(type, op, args));
    }

    @Override
    public Operator getOperator() {
        return opMixin.getOperator();
    }

    @Override
    public Expression<?> getArg(int index) {
        return opMixin.getArg(index);
    }

    @Override
    public Optional<Expression<?>> getOptArg(int index) {
        return opMixin.getOptArg(index);
    }

    @Override
    public List<Expression<?>> getArgs() {
        return opMixin.getArgs();
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(opMixin, context);
    }
}

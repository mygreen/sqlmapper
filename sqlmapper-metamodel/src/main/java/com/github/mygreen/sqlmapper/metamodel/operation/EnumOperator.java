package com.github.mygreen.sqlmapper.metamodel.operation;

import java.util.List;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.EnumExpression;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

import lombok.NonNull;

public class EnumOperator<T extends Enum<T>> extends EnumExpression<T> implements Operation<T> {

    private final OperationMixin<T> opMixin;

    protected EnumOperator(OperationMixin<T> mixin) {
        super(mixin);
        this.opMixin = mixin;
    }

    public EnumOperator(Class<? extends T> type, @NonNull Operator op, Expression<?>... args) {
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
    public List<Expression<?>> getArgs() {
        return opMixin.getArgs();
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(opMixin, context);
    }
}
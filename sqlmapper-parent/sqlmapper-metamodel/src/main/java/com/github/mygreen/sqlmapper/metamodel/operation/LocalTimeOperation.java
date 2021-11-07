package com.github.mygreen.sqlmapper.metamodel.operation;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.expression.LocalTimeExpression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

import lombok.NonNull;


/**
 * {@link LocalTime} を値に持つ式の演算操作を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class LocalTimeOperation extends LocalTimeExpression implements Operation<LocalTime> {

    private final OperationMixin<LocalTime> opMixin;

    public LocalTimeOperation(OperationMixin<LocalTime> mixin) {
        super(mixin);
        this.opMixin = mixin;
    }

    public LocalTimeOperation(@NonNull Operator op, Expression<?>... args) {
        this(new OperationMixin<>(LocalTime.class, op, args));
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(opMixin, context);
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
}

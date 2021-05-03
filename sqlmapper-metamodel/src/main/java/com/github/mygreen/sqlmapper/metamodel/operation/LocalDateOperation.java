package com.github.mygreen.sqlmapper.metamodel.operation;

import java.time.LocalDate;
import java.util.List;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.expression.LocalDateExpression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

import lombok.NonNull;

/**
 * {@link LocalDate} を値に持つ式の演算操作を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class LocalDateOperation extends LocalDateExpression implements Operation<LocalDate> {

    private final OperationMixin<LocalDate> opMixin;

    public LocalDateOperation(OperationMixin<LocalDate> mixin) {
        super(mixin);
        this.opMixin = mixin;
    }

    public LocalDateOperation(@NonNull Operator op, Expression<?>... args) {
        this(new OperationMixin<>(LocalDate.class, op, args));
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
    public List<Expression<?>> getArgs() {
        return opMixin.getArgs();
    }
}

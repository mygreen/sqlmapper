package com.github.mygreen.sqlmapper.metamodel.operation;

import java.time.LocalDateTime;
import java.util.List;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.expression.LocalDateTimeExpression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

import lombok.NonNull;

public class LocalDateTimeOperation extends LocalDateTimeExpression implements Operation<LocalDateTime> {

    private final OperationMixin<LocalDateTime> opMixin;

    public LocalDateTimeOperation(OperationMixin<LocalDateTime> mixin) {
        super(mixin);
        this.opMixin = mixin;
    }

    public LocalDateTimeOperation(@NonNull Operator op, Expression<?>... args) {
        this(new OperationMixin<>(LocalDateTime.class, op, args));
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

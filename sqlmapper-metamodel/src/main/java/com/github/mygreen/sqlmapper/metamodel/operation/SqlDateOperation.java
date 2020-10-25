package com.github.mygreen.sqlmapper.metamodel.operation;

import java.sql.Date;
import java.util.List;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.expression.SqlDateExpression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

import lombok.NonNull;

public class SqlDateOperation extends SqlDateExpression implements Operation<Date> {

    private final OperationMixin<Date> opMixin;

    public SqlDateOperation(OperationMixin<Date> mixin) {
        super(mixin);
        this.opMixin = mixin;
    }

    public SqlDateOperation(@NonNull Operator op, Expression<?>... args) {
        this(new OperationMixin<>(Date.class, op, args));
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

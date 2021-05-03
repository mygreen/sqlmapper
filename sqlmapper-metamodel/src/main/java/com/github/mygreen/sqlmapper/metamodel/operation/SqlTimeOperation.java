package com.github.mygreen.sqlmapper.metamodel.operation;

import java.sql.Time;
import java.util.List;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.expression.SqlTimeExpression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

import lombok.NonNull;


/**
 * {@link Time} を値に持つ式の演算操作を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlTimeOperation extends SqlTimeExpression implements Operation<Time> {

    private final OperationMixin<Time> opMixin;

    public SqlTimeOperation(OperationMixin<Time> mixin) {
        super(mixin);
        this.opMixin = mixin;
    }

    public SqlTimeOperation(@NonNull Operator op, Expression<?>... args) {
        this(new OperationMixin<>(Time.class, op, args));
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

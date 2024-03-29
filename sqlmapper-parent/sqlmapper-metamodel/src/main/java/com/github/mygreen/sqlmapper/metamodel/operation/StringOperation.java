package com.github.mygreen.sqlmapper.metamodel.operation;

import java.util.List;
import java.util.Optional;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.expression.StringExpression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

import lombok.NonNull;

/**
 * 文字列型を値に持つ式の演算操作を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class StringOperation extends StringExpression implements Operation<String> {

    private final OperationMixin<String> opMixin;

    protected StringOperation(OperationMixin<String> mixin) {
        super(mixin);
        this.opMixin = mixin;
    }

    public StringOperation(@NonNull Operator op, Expression<?>... args) {
        this(new OperationMixin<>(String.class, op, args));
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

package com.github.mygreen.sqlmapper.metamodel.operation;

import java.util.List;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.expression.GeneralExpression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

import lombok.NonNull;

/**
 * 汎用的な型を値に持つ式の演算操作を表現します。
 * <p>{@literal byte[]} 型など専用の式の型がないときに用います。
 *
 * @author T.TSUCHIE
 *
 * @param <T> 式のタイプ
 */
public class GeneralOperation<T> extends GeneralExpression<T> implements Operation<T> {

    private final OperationMixin<T> opMixin;

    protected GeneralOperation(OperationMixin<T> mixin) {
        super(mixin);
        this.opMixin = mixin;
    }

    public GeneralOperation(Class<? extends T> type, @NonNull Operator op, Expression<?>... args) {
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

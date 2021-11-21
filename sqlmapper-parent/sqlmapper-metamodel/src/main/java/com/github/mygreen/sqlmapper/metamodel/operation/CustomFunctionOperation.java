package com.github.mygreen.sqlmapper.metamodel.operation;

import java.util.Optional;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.CustomFuntionExpression;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operator.FunctionOp;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;


/**
 * {@link CustomFuntionExpression}の実装クラスです。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class CustomFunctionOperation extends CustomFuntionExpression implements Operation {

    @SuppressWarnings("unchecked")
    public CustomFunctionOperation(Expression<?> mixin, String query, Object... args) {
        super(mixin, query, args);
    }

    @Override
    public Class<?> getType() {
        return Object.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void accept(Visitor visitor, Object context) {
        visitor.visit(this, context);
    }

    @Override
    public Operator getOperator() {
        return FunctionOp.CUSTOM;
    }

    @Override
    public Expression<?> getArg(int index) {
        return (Expression)args.get(index);
    }

    @Override
    public Optional<Expression<?>> getOptArg(int index) {
        if(index >= args.size()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable((Expression)args.get(index));
        }
    }

}

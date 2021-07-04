package com.github.mygreen.sqlmapper.metamodel.operation;

import java.util.List;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.BooleanExpression;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;
import com.github.mygreen.sqlmapper.metamodel.operator.UnaryOp;

import lombok.NonNull;

/**
 * ブーリアン型を値に持つ式の演算操作を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class BooleanOperation extends BooleanExpression implements Operation<Boolean> {

    private PredicateOperation opMixin;

    public BooleanOperation(PredicateOperation mixin) {
        super(mixin);
        this.opMixin = mixin;
    }

    public BooleanOperation(@NonNull Operator op, Expression<?>... args) {
        this(new PredicateOperation(op, args));
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

    /**
     * {@inheritDoc}
     *
     * @return 2重否定の場合は、否定を取り消した式を返します。
     */
    @Override
    public BooleanExpression not() {

        if(opMixin.getOperator() == UnaryOp.NOT && opMixin.getArg(0) instanceof BooleanOperation) {
            // 子要素がNOTの場合は2重否定になるため、否定を取り外す
            return (BooleanOperation) opMixin.getArg(0);
        }
        return super.not();
    }

}

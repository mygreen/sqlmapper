package com.github.mygreen.sqlmapper.metamodel.expression;

import java.time.LocalDate;

import com.github.mygreen.sqlmapper.metamodel.operation.LocalDateOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;

public abstract class LocalDateExpression extends TemporalExpressionBase<LocalDate> {

    public LocalDateExpression(Expression<LocalDate> mixin) {
        super(mixin);
    }

    public static LocalDateExpression currentDate() {
        return new LocalDateOperation(FuncOp.CURRENT_DATE);
    }
}

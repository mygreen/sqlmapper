package com.github.mygreen.sqlmapper.metamodel.expression;

import java.time.LocalTime;

import com.github.mygreen.sqlmapper.metamodel.operation.LocalTimeOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;

public abstract class LocalTimeExpression extends TemporalExpressionBase<LocalTime> {

    public LocalTimeExpression(Expression<LocalTime> mixin) {
        super(mixin);
    }

    public static LocalTimeExpression currentTime() {
        return new LocalTimeOperation(FuncOp.CURRENT_TIME);
    }
}

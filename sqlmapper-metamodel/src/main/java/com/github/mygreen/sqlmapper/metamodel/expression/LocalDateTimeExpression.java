package com.github.mygreen.sqlmapper.metamodel.expression;

import java.time.LocalDateTime;

import com.github.mygreen.sqlmapper.metamodel.operation.LocalDateTimeOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;

public abstract class LocalDateTimeExpression extends TemporalExpressionBase<LocalDateTime> {

    public LocalDateTimeExpression(Expression<LocalDateTime> mixin) {
        super(mixin);
    }

    public static LocalDateTimeExpression currentDateTime() {
        return new LocalDateTimeOperation(FuncOp.CURRENT_TIMESTAMP);
    }

    public static LocalDateTimeExpression currentTimestamp(int precision ) {
        return new LocalDateTimeOperation(FuncOp.CURRENT_TIMESTAMP, Constant.create(precision));
    }

}

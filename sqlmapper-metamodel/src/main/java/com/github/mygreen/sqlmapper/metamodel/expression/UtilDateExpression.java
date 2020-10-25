package com.github.mygreen.sqlmapper.metamodel.expression;

import java.util.Date;

import com.github.mygreen.sqlmapper.metamodel.operation.UtilDateOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;

public abstract class UtilDateExpression extends DateExpressionBase<Date> {

    public UtilDateExpression(Expression<Date> mixin) {
        super(mixin);
    }

    public static UtilDateExpression currentDateTime() {
        return new UtilDateOperation(FuncOp.CURRENT_DATE_TIME);
    }
}

package com.github.mygreen.sqlmapper.metamodel.expression;

import java.sql.Date;

import com.github.mygreen.sqlmapper.metamodel.operation.SqlDateOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;

public abstract class SqlDateExpression extends DateExpressionBase<Date> {

    public SqlDateExpression(Expression<Date> mixin) {
        super(mixin);
    }

    public static SqlDateExpression currentDate() {
        return new SqlDateOperation(FuncOp.CURRENT_DATE);
    }
}

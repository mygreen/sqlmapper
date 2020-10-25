package com.github.mygreen.sqlmapper.metamodel.expression;

import java.sql.Timestamp;

import com.github.mygreen.sqlmapper.metamodel.operation.SqlTimestampOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;

public abstract class SqlTimestampExpression extends DateExpressionBase<Timestamp> {

    public SqlTimestampExpression(Expression<Timestamp> mixin) {
        super(mixin);
    }

    public static SqlTimestampExpression currentTimestamp() {
        return new SqlTimestampOperation(FuncOp.CURRENT_TIMESTAMP);
    }
}

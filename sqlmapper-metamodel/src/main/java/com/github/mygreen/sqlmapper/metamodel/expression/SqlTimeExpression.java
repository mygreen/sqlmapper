package com.github.mygreen.sqlmapper.metamodel.expression;

import java.sql.Time;

import com.github.mygreen.sqlmapper.metamodel.operation.SqlTimeOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;

public abstract class SqlTimeExpression extends DateExpressionBase<Time> {

    public SqlTimeExpression(Expression<Time> mixin) {
        super(mixin);
    }

    public static SqlTimeExpression currentTime() {
        return new SqlTimeOperation(FuncOp.CURRENT_TIME);
    }

    public static SqlTimeExpression currentTime(int precision ) {
        return new SqlTimeOperation(FuncOp.CURRENT_TIME, Constant.create(precision));
    }

}

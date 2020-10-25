package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.operation.BooleanOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.StringOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.BinaryOp;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;

public abstract class StringExpression extends ComparableExpression<String> {

    public StringExpression(Expression<String> mixin) {
        super(mixin);
    }

    public BooleanExpression contains(String str) {
        return contains(Constant.create(str));
    }

    public BooleanExpression contains(Expression<String> str) {
        return new BooleanOperation(BinaryOp.CONTAINS, mixin, str);
    }

    public StringExpression lower() {
        return new StringOperation(FuncOp.LOWER, mixin);
    }

    public StringExpression upper() {
        return new StringOperation(FuncOp.UPPER, mixin);
    }
}

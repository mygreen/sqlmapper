package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.operation.NumberOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.StringOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.EnumOp;

public abstract class EnumExpression<T extends Enum<T>> extends GeneralExpression<T> {

    public EnumExpression(Expression<T> mixin) {
        super(mixin);
    }

    public NumberExpression<Integer> ordinal() {
        return new NumberOperation<Integer>(Integer.class, EnumOp.ENUM_ORDINAL, mixin);
    }

    public StringExpression name() {
        return new StringOperation(EnumOp.ENUM_ORDINAL, mixin);
    }

}

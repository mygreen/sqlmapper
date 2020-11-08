package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.operation.BooleanOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.StringOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;
import com.github.mygreen.sqlmapper.metamodel.operator.LikeOp;

public abstract class StringExpression extends ComparableExpression<String> {

    public StringExpression(Expression<String> mixin) {
        super(mixin);
    }

    /**
     *
     * 特殊文字の挿入やエスケープは予め実施しておく必要があります。
     * @param str LIKE検索対象の文字列。
     * @return
     */
    public BooleanExpression like(String str) {
        return like(Constant.create(str));
    }

    protected BooleanExpression like(Expression<String> str) {
        return new BooleanOperation(LikeOp.LIKE, mixin, str);
    }

    public BooleanExpression contains(String str) {
        return contains(Constant.create(str));
    }

    protected BooleanExpression contains(Expression<String> str) {
        return new BooleanOperation(LikeOp.CONTAINS, mixin, str);
    }

    public BooleanExpression starts(String str) {
        return starts(Constant.create(str));
    }

    protected BooleanExpression starts(Expression<String> str) {
        return new BooleanOperation(LikeOp.STARTS, mixin, str);
    }

    public BooleanExpression ends(String str) {
        return ends(Constant.create(str));
    }

    protected BooleanExpression ends(Expression<String> str) {
        return new BooleanOperation(LikeOp.ENDS, mixin, str);
    }

    public StringExpression lower() {
        return new StringOperation(FuncOp.LOWER, mixin);
    }

    public StringExpression upper() {
        return new StringOperation(FuncOp.UPPER, mixin);
    }
}

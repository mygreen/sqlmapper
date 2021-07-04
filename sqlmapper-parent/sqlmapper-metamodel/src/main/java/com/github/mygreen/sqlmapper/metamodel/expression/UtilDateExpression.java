package com.github.mygreen.sqlmapper.metamodel.expression;

import java.util.Date;

import com.github.mygreen.sqlmapper.metamodel.operation.UtilDateOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FunctionOp;

/**
 * {@link Date} による式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class UtilDateExpression extends DateExpressionBase<Date> {

    public UtilDateExpression(Expression<Date> mixin) {
        super(mixin);
    }

    /**
     * 現在の日時を取得する関数 {@literal CURRENT_TIMESTAMP} を返します。
     * @return 関数 {@literal CURRENT_TIMESTAMP}
     */
    public static UtilDateExpression currentTimestamp() {
        return new UtilDateOperation(FunctionOp.CURRENT_TIMESTAMP);
    }

    /**
     * 精度を指定して、現在の日時を取得する関数 {@literal CURRENT_TIMESTAMP} を返します。
     * @param precision 精度
     * @return 関数 {@literal CURRENT_TIMESTAMP}
     */
    public static UtilDateExpression currentTimestamp(int precision ) {
        return new UtilDateOperation(FunctionOp.CURRENT_TIMESTAMP, Constant.create(precision));
    }
}

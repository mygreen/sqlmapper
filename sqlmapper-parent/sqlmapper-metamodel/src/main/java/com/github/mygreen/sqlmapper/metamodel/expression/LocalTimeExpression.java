package com.github.mygreen.sqlmapper.metamodel.expression;

import java.time.LocalTime;

import com.github.mygreen.sqlmapper.metamodel.operation.LocalTimeOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FunctionOp;

/**
 * {@link LocalTime} による式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class LocalTimeExpression extends TemporalExpressionBase<LocalTime> {

    public LocalTimeExpression(Expression<LocalTime> mixin) {
        super(mixin);
    }

    /**
     * 現在の時刻を取得する関数 {@literal CURRENT_TIME} を返します。
     * @return 関数 {@literal CURRENT_TIME}
     */
    public static LocalTimeExpression currentTime() {
        return new LocalTimeOperation(FunctionOp.CURRENT_TIME);
    }

    /**
     * 精度を指定して、現在の時刻を取得する関数 {@literal CURRENT_TIME} を返します。
     * @param precision 精度
     * @return 関数 {@literal CURRENT_TIME}
     */
    public static LocalTimeExpression currentTime(int precision) {
        return new LocalTimeOperation(FunctionOp.CURRENT_TIME, Constant.create(precision));
    }
}

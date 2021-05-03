package com.github.mygreen.sqlmapper.metamodel.expression;

import java.time.LocalDateTime;

import com.github.mygreen.sqlmapper.metamodel.operation.LocalDateTimeOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;

/**
 * {@link LocalDateTime} による式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class LocalDateTimeExpression extends TemporalExpressionBase<LocalDateTime> {

    public LocalDateTimeExpression(Expression<LocalDateTime> mixin) {
        super(mixin);
    }

    /**
     * 現在の日時を取得する関数 {@literal CURRENT_TIMESTAMP} を返します。
     * @return 関数 {@literal CURRENT_TIMESTAMP}
     */
    public static LocalDateTimeExpression currentDateTime() {
        return new LocalDateTimeOperation(FuncOp.CURRENT_TIMESTAMP);
    }

    /**
     * 精度を指定して、現在の日時を取得する関数 {@literal CURRENT_TIMESTAMP} を返します。
     * @param precision 精度
     * @return 関数 {@literal CURRENT_TIMESTAMP}
     */
    public static LocalDateTimeExpression currentTimestamp(int precision) {
        return new LocalDateTimeOperation(FuncOp.CURRENT_TIMESTAMP, Constant.create(precision));
    }

}

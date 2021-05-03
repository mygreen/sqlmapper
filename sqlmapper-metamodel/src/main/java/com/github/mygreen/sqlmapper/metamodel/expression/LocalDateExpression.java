package com.github.mygreen.sqlmapper.metamodel.expression;

import java.time.LocalDate;

import com.github.mygreen.sqlmapper.metamodel.operation.LocalDateOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;

/**
 * {@link LocalDate} による式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class LocalDateExpression extends TemporalExpressionBase<LocalDate> {

    public LocalDateExpression(Expression<LocalDate> mixin) {
        super(mixin);
    }

    /**
     * 現在の日付を取得する関数 {@literal CURRENT_DATE} を返します。
     * @return 関数 {@literal CURRENT_DATE}
     */
    public static LocalDateExpression currentDate() {
        return new LocalDateOperation(FuncOp.CURRENT_DATE);
    }
}

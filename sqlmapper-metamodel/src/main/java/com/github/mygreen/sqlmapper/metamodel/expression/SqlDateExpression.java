package com.github.mygreen.sqlmapper.metamodel.expression;

import java.sql.Date;

import com.github.mygreen.sqlmapper.metamodel.operation.SqlDateOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FunctionOp;


/**
 * {@link Date} による式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class SqlDateExpression extends DateExpressionBase<Date> {

    public SqlDateExpression(Expression<Date> mixin) {
        super(mixin);
    }

    /**
     * 現在の日付を取得する関数 {@literal CURRENT_DATE} を返します。
     * @return 関数 {@literal CURRENT_DATE}
     */
    public static SqlDateExpression currentDate() {
        return new SqlDateOperation(FunctionOp.CURRENT_DATE);
    }
}

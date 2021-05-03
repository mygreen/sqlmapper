package com.github.mygreen.sqlmapper.metamodel.expression;

import java.sql.Timestamp;

import com.github.mygreen.sqlmapper.metamodel.operation.SqlTimestampOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;


/**
 * {@link Timestamp} による式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class SqlTimestampExpression extends DateExpressionBase<Timestamp> {

    public SqlTimestampExpression(Expression<Timestamp> mixin) {
        super(mixin);
    }

    /**
     * 現在の日時を取得する関数 {@literal CURRENT_TIMESTAMP} を返します。
     * @return 関数 {@literal CURRENT_TIMESTAMP}
     */
    public static SqlTimestampExpression currentTimestamp() {
        return new SqlTimestampOperation(FuncOp.CURRENT_TIMESTAMP);
    }

    /**
     * 精度を指定して、現在の日時を取得する関数 {@literal CURRENT_TIMESTAMP} を返します。
     * @param precision 精度
     * @return 関数 {@literal CURRENT_TIMESTAMP}
     */
    public static SqlTimestampExpression currentTimestamp(int precision) {
        return new SqlTimestampOperation(FuncOp.CURRENT_TIMESTAMP, Constant.create(precision));
    }
}

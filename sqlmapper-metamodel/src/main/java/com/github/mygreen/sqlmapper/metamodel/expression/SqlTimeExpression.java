package com.github.mygreen.sqlmapper.metamodel.expression;

import java.sql.Time;

import com.github.mygreen.sqlmapper.metamodel.operation.SqlTimeOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;

/**
 * {@link Time} による式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class SqlTimeExpression extends DateExpressionBase<Time> {

    public SqlTimeExpression(Expression<Time> mixin) {
        super(mixin);
    }

    /**
     * 現在の時刻を取得する関数 {@literal CURRENT_TIME} を返します。
     * @return 関数 {@literal CURRENT_TIME}
     */
    public static SqlTimeExpression currentTime() {
        return new SqlTimeOperation(FuncOp.CURRENT_TIME);
    }

    /**
     * 精度を指定して、現在の時刻を取得する関数 {@literal CURRENT_TIME} を返します。
     * @param precision 精度
     * @return 関数 {@literal CURRENT_TIME}
     */
    public static SqlTimeExpression currentTime(int precision) {
        return new SqlTimeOperation(FuncOp.CURRENT_TIME, Constant.create(precision));
    }

}

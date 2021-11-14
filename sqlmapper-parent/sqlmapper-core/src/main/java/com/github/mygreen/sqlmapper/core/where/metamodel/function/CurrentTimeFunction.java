package com.github.mygreen.sqlmapper.core.where.metamodel.function;

import java.util.List;

import com.github.mygreen.sqlmapper.core.where.metamodel.ExpressionEvaluator;
import com.github.mygreen.sqlmapper.core.where.metamodel.SqlFunction;
import com.github.mygreen.sqlmapper.core.where.metamodel.VisitorContext;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;

/**
 * {@literal CURRENT_TIME} 関数を処理します。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class CurrentTimeFunction implements SqlFunction {

    @Override
    public void handle(List<Expression<?>> args, Visitor<VisitorContext> visitor, VisitorContext context,
            ExpressionEvaluator invoker) {

        context.appendSql("current_time");
        doPrecision(args, context);

    }

    /**
     * 時間の制度が指定されていれば処理を行う。
     * @param expr 演算子の式
     * @param context コンテキスト
     */
    @SuppressWarnings("unchecked")
    private void doPrecision(List<Expression<?>> args, VisitorContext context) {

        if(args.isEmpty()) {
            return;
        }

        int precision = (int)((Constant<Integer>)args.get(0)).getValue();
        context.appendSql("(").append(precision).append(")");

    }

}

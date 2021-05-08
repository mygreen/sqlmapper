package com.github.mygreen.sqlmapper.core.where.metamodel;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operator.FunctionOp;

/**
 * 関数({@link FunctionOp})に対する処理を定義します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class FuncOpHandler extends OperationHandler<FunctionOp>{

    @Override
    protected void init() {
        // 何もしない
    }

    @Override
    public void handle(FunctionOp operator, Operation<?> expr, Visitor<VisitorContext> visitor, VisitorContext context) {

        switch(operator) {
            case LOWER:
                context.appendSql("lower(");
                invoke(operator, expr.getArg(0), visitor, context);
                context.appendSql(")");
                break;
            case UPPER:
                context.appendSql("upper(");
                invoke(operator, expr.getArg(0), visitor, context);
                context.appendSql(")");
                break;
            case CURRENT_DATE:
                context.appendSql("current_date");
                doPrecision(expr, context);
                break;
            case CURRENT_TIME:
                context.appendSql("current_time");
                doPrecision(expr, context);
                break;
            case CURRENT_TIMESTAMP:
                context.appendSql("current_timestamp");
                doPrecision(expr, context);
                break;
            default:
                throw new IllegalArgumentException("not support operator=" + operator);
        }
    }

    /**
     * 時間の制度が指定されていれば処理を行う。
     * @param expr 演算子の式
     * @param context コンテキスト
     */
    @SuppressWarnings("unchecked")
    private void doPrecision(Operation<?> expr, VisitorContext context) {

        if(expr.getArgs().isEmpty()) {
            return;
        }

        int precision = (int)((Constant<Integer>)expr.getArg(0)).getValue();
        context.appendSql("(").append(precision).append(")");

    }

}

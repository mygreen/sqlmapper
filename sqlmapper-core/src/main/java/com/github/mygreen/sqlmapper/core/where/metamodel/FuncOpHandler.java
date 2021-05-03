package com.github.mygreen.sqlmapper.core.where.metamodel;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;

/**
 * 関数({@link FuncOp})に対する処理を定義します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class FuncOpHandler extends OperationHandler<FuncOp>{

    @Override
    protected void init() {
        // 何もしない
    }

    @Override
    public void handle(FuncOp operator, Operation<?> expr, Visitor<VisitorContext> visitor, VisitorContext context) {

        switch(operator) {
            case LOWER:
                context.appendSql("LOWER(");
                invoke(operator, expr.getArg(0), visitor, context);
                context.appendSql(")");
                break;
            case UPPER:
                context.appendSql("UPPER(");
                invoke(operator, expr.getArg(0), visitor, context);
                context.appendSql(")");
                break;
            case CURRENT_DATE:
                context.appendSql("CURRENT_DATE");
                doPrecision(expr, context);
                break;
            case CURRENT_TIME:
                context.appendSql("CURRENT_TIME");
                doPrecision(expr, context);
                break;
            case CURRENT_TIMESTAMP:
                context.appendSql("CURRENT_TIMESTAMP");
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

package com.github.mygreen.sqlmapper.core.where.metamodel;

import java.util.HashMap;
import java.util.Map;

import com.github.mygreen.sqlmapper.core.where.metamodel.function.ConcatFunction;
import com.github.mygreen.sqlmapper.core.where.metamodel.function.CurrentDateFunction;
import com.github.mygreen.sqlmapper.core.where.metamodel.function.CurrentTimeFunction;
import com.github.mygreen.sqlmapper.core.where.metamodel.function.CurrentTimestampFunction;
import com.github.mygreen.sqlmapper.core.where.metamodel.function.CustomFunction;
import com.github.mygreen.sqlmapper.core.where.metamodel.function.LowerFunction;
import com.github.mygreen.sqlmapper.core.where.metamodel.function.UpperFunction;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operator.FunctionOp;

/**
 * 関数({@link FunctionOp})に対する処理を定義します。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public class FuncOpHandler extends OperationHandler<FunctionOp> {

    /**
     * SQL関数の各処理に対する実装
     */
    private Map<FunctionOp, SqlFunction> functionMap = new HashMap<>();

    /**
     * SQL関数の処理を登録します。
     * @since 0.3
     * @param operator SQL関数名
     * @param function SQL関数の処理
     */
    public void register(FunctionOp operator, SqlFunction function) {
        this.functionMap.put(operator, function);
    }

    @Override
    protected void init() {
        this.functionMap.clear();
        register(FunctionOp.LOWER, new LowerFunction());
        register(FunctionOp.UPPER, new UpperFunction());

        register(FunctionOp.CONCAT, new ConcatFunction());

        register(FunctionOp.CURRENT_DATE, new CurrentDateFunction());
        register(FunctionOp.CURRENT_TIME, new CurrentTimeFunction());
        register(FunctionOp.CURRENT_TIMESTAMP, new CurrentTimestampFunction());

        register(FunctionOp.CUSTOM, new CustomFunction());
    }

    @Override
    public void handle(FunctionOp operator, Operation<?> expr, Visitor<VisitorContext> visitor, VisitorContext context) {

        SqlFunction function = functionMap.get(operator);
        if(function == null) {
            throw new IllegalArgumentException("not support operator=" + operator);
        }

        function.handle(expr.getArgs(), visitor, context, new ExpressionEvaluator(operator, this));

    }

}

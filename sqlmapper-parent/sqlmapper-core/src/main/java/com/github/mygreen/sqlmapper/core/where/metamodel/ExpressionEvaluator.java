package com.github.mygreen.sqlmapper.core.where.metamodel;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operator.FunctionOp;

import lombok.RequiredArgsConstructor;

/**
 * 演算子を評価するための処理。
 * <p>SQL関数を評価する際にサブ式を評価するために使用します。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class ExpressionEvaluator {

    private final FunctionOp operator;

    private final OperationHandler<?> handler;

    /**
     * 式ノードを評価し、SQLを組み立てます。
     *
     * @param expr 評価対象の式
     * @param visitor Visitor
     * @param context コンテキスト
     */
    public void evaluate(Expression<?> expr, Visitor<VisitorContext> visitor, VisitorContext context) {
        this.handler.invoke(operator, expr, visitor, context);
    }

}

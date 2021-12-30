package com.github.mygreen.sqlmapper.core.where.metamodel.function;

import java.util.List;

import com.github.mygreen.sqlmapper.core.where.metamodel.ExpressionEvaluator;
import com.github.mygreen.sqlmapper.core.where.metamodel.SqlFunction;
import com.github.mygreen.sqlmapper.core.where.metamodel.VisitorContext;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;

/**
 * {@literal CONCAT} 関数を処理します。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class ConcatFunction implements SqlFunction {

    @Override
    public void handle(List<Expression<?>> args, Visitor<VisitorContext> visitor, VisitorContext context,
            ExpressionEvaluator evaluator) {

        // 左辺の評価
        Expression<?> left = args.get(0);
        VisitorContext leftContext = new VisitorContext(context);
        evaluator.evaluate(left, visitor, leftContext);

        // 右辺の評価
        Expression<?> right = args.get(1);
        VisitorContext rightContext = new VisitorContext(context);
        evaluator.evaluate(right, visitor, rightContext);

        // 評価した結果を親のコンテキストに追加する
        context.appendSql("concat(")
            .append(leftContext.getCriteria())
            .append(", ")
            .append(rightContext.getCriteria())
            .append(")");

        context.addParamValues(leftContext.getParamValues());
        context.addParamValues(rightContext.getParamValues());

    }
}

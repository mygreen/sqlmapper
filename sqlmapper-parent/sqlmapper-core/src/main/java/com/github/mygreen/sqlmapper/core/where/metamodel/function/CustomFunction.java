package com.github.mygreen.sqlmapper.core.where.metamodel.function;

import java.util.List;

import com.github.mygreen.sqlmapper.core.where.metamodel.ExpressionEvaluator;
import com.github.mygreen.sqlmapper.core.where.metamodel.SqlFunction;
import com.github.mygreen.sqlmapper.core.where.metamodel.VisitorContext;
import com.github.mygreen.sqlmapper.core.where.metamodel.function.SqlFunctionTokenizer.TokenType;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operation.CustomFunctionOperation;

/**
 * 任意の関数を処理します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class CustomFunction implements SqlFunction {

    @Override
    public void handle(List<Expression<?>> args, Visitor<VisitorContext> visitor,
            VisitorContext context, ExpressionEvaluator evaluator) {

        // 左辺の評価
        Expression<?> left = args.get(0);
        VisitorContext leftContext = new VisitorContext(context);
        evaluator.evaluate(left, visitor, leftContext);

        CustomFunctionOperation op = (CustomFunctionOperation) args.get(1);

        String query = op.getQuery();

        // クエリを軸解析して、置換していく。
        SqlFunctionTokenizer tokenizer = new SqlFunctionTokenizer(query);
        while(TokenType.EOF != tokenizer.next()) {
            TokenType currentToken =tokenizer.getTokenType();
            if(currentToken == TokenType.SQL) {
                context.appendSql(tokenizer.getToken());

            } else if(currentToken == TokenType.THIS_VARIABLE) {
                context.appendSql(leftContext.getCriteria());
                context.addParamValues(leftContext.getParamValues());

            } else if(currentToken == TokenType.BIND_VARIABLE) {
                int varIndex = tokenizer.getBindBariableNum() - 1;
                Expression<?> arg = op.getArg(varIndex);

                VisitorContext argContext = new VisitorContext(context);
                evaluator.evaluate(arg, visitor, argContext);

                if(arg instanceof Constant) {
                    // 定数の場合はプレースホルダーとして変数として追加
                    context.appendSql("?");
                } else {
                    context.appendSql(argContext.getCriteria());
                }

                context.addParamValues(argContext.getParamValues());

            } else {
                // unknown
            }
        }

    }


}

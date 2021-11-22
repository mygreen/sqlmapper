package com.github.mygreen.sqlmapper.core.where.metamodel.function;

import java.util.List;

import com.github.mygreen.sqlmapper.core.where.metamodel.ExpressionEvaluator;
import com.github.mygreen.sqlmapper.core.where.metamodel.SqlFunction;
import com.github.mygreen.sqlmapper.core.where.metamodel.VisitorContext;
import com.github.mygreen.sqlmapper.core.where.metamodel.function.SqlFunctionTokenizer.TokenType;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operation.CustomFunctionOperation;

/**
 * 任意の関数を処理します。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class CustomFunction implements SqlFunction {

    @Override
    public void handle(List<Expression<?>> args, Visitor<VisitorContext> visitor,
            VisitorContext context, ExpressionEvaluator evaluator) {

        // 自身プロパティの評価
        Expression<?> thisExp = args.get(0);
        VisitorContext thisContext = new VisitorContext(context);
        evaluator.evaluate(thisExp, visitor, thisContext);

        CustomFunctionOperation op = (CustomFunctionOperation) args.get(1);

        String query = op.getQuery();

        // クエリを軸解析して、置換していく。
        SqlFunctionTokenizer tokenizer = new SqlFunctionTokenizer(query);
        while(TokenType.EOF != tokenizer.next()) {
            TokenType currentToken =tokenizer.getTokenType();
            if(currentToken == TokenType.SQL) {
                context.appendSql(tokenizer.getToken());

            } else if(currentToken == TokenType.THIS_VARIABLE) {
                context.appendSql(thisContext.getCriteria());
                context.addParamValues(thisContext.getParamValues());

            } else if(currentToken == TokenType.BIND_VARIABLE) {
                int varIndex = tokenizer.getBindBariableNum() - 1;
                Expression<?> arg = op.getArg(varIndex);

                VisitorContext argContext = new VisitorContext(context);
                evaluator.evaluate(arg, visitor, argContext);

                context.appendSql(argContext.getCriteria());
                context.addParamValues(argContext.getParamValues());

            } else {
                // unknown
            }
        }

    }


}

package com.github.mygreen.sqlmapper.core.where.metamodel.function;

import java.util.List;

import com.github.mygreen.sqlmapper.core.where.metamodel.ExpressionEvaluator;
import com.github.mygreen.sqlmapper.core.where.metamodel.SqlFunction;
import com.github.mygreen.sqlmapper.core.where.metamodel.VisitorContext;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operation.CustomFunctionOperation;
import com.github.mygreen.sqlmapper.metamodel.support.SqlFunctionParser.Token;
import com.github.mygreen.sqlmapper.metamodel.support.SqlFunctionTokenizer.TokenType;

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

        // 左辺の評価
        Expression<?> left = args.get(0);
        VisitorContext leftContext = new VisitorContext(context);
        evaluator.evaluate(left, visitor, leftContext);

        CustomFunctionOperation op = (CustomFunctionOperation) args.get(1);

        // トークンを置換していく
        @SuppressWarnings("unchecked")
        List<Token> tokens = op.getTokens();
        for(Token token : tokens) {
            if(token.type == TokenType.SQL) {
                context.appendSql(token.value);

            } else if(token.type == TokenType.LEFT_VARIABLE) {
                context.appendSql(leftContext.getCriteria());
                context.addParamValues(leftContext.getParamValues());

            } else if(token.type == TokenType.BIND_VARIABLE) {
                int varIndex = token.bindBariableIndex;
                Expression<?> arg = op.getArg(varIndex);

                VisitorContext argContext = new VisitorContext(context);
                evaluator.evaluate(arg, visitor, argContext);

                context.appendSql(argContext.getCriteria());
                context.addParamValues(argContext.getParamValues());

            } else {
                // unknown token
            }

        }

    }


}

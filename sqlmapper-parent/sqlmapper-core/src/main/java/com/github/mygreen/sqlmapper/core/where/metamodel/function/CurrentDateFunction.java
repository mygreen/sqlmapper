package com.github.mygreen.sqlmapper.core.where.metamodel.function;

import java.util.List;

import com.github.mygreen.sqlmapper.core.where.metamodel.ExpressionEvaluator;
import com.github.mygreen.sqlmapper.core.where.metamodel.SqlFunction;
import com.github.mygreen.sqlmapper.core.where.metamodel.VisitorContext;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;

/**
 * {@literal CURRENT_DATE} 関数を処理します。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class CurrentDateFunction implements SqlFunction {

    @Override
    public void handle(List<Expression<?>> args, Visitor<VisitorContext> visitor, VisitorContext context,
            ExpressionEvaluator invoker) {

        context.appendSql("current_date");

    }
}

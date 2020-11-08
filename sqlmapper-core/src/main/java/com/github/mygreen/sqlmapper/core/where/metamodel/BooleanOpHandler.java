package com.github.mygreen.sqlmapper.core.where.metamodel;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operator.BooleanOp;

public class BooleanOpHandler extends OperationHandler<BooleanOp> {

    @Override
    protected void init() {
        addTemplate(BooleanOp.AND, "{0} AND {1}");
        addTemplate(BooleanOp.OR, "{0} OR {1}");
    }

    @Override
    public void handle(BooleanOp operator, Operation<?> expr, Visitor<VisitorContext> visitor, VisitorContext context) {

        Expression<?> left = expr.getArg(0);
        Expression<?> right = expr.getArg(1);

        VisitorContext leftContext = new VisitorContext(context);
        VisitorContext rightContext = new VisitorContext(context);

        // 左辺の評価
        invoke(operator, left, visitor, leftContext);
        invoke(operator, right, visitor, rightContext);

        // 評価した結果を親のコンテキストに追加する
        String sql = formatWithTemplate(operator, leftContext.getCriteria(), rightContext.getCriteria());
        context.appendSql(sql);

        context.addParamValues(leftContext.getParamValues());
        context.addParamValues(rightContext.getParamValues());

    }


}

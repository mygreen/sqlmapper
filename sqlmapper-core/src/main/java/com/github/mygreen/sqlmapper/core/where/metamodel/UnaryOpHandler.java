package com.github.mygreen.sqlmapper.core.where.metamodel;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operator.UnaryOp;

public class UnaryOpHandler extends OperationHandler<UnaryOp> {

    @Override
    protected void init() {
        addTemplate(UnaryOp.NOT, "NOT {0}");
        addTemplate(UnaryOp.IS_NULL, "{0} IS NULL");
        addTemplate(UnaryOp.IS_NOT_NULL, "{0} IS NOT NULL");
    }

    @Override
    public void handle(UnaryOp operator, Operation<?> expr, Visitor<VisitorContext> visitor, VisitorContext context) {

        Expression<?> left = expr.getArg(0);

        VisitorContext leftContext = new VisitorContext(context);

        // 左辺の評価
        invoke(operator, left, visitor, leftContext);

        // 評価した結果を親のコンテキストに追加する
        String sql = formatWithTemplate(operator, leftContext.getCriteria());
        context.appendSql(sql);
        context.addParamValues(leftContext.getParamValues());

    }

}
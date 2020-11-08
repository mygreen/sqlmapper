package com.github.mygreen.sqlmapper.core.where.metamodel;

import com.github.mygreen.sqlmapper.metamodel.PropertyPath;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operator.ComparisionOp;

public class ComparisionOpHandler extends OperationHandler<ComparisionOp> {

    @Override
    protected void init() {
        // 比較演算子 - General
        addTemplate(ComparisionOp.EQ, "{0} = {1}");
        addTemplate(ComparisionOp.NE, "{0} <> {1}");
        addTemplate(ComparisionOp.IN, "{0} IN {1}");
        addTemplate(ComparisionOp.NOT_IN, "{0} NOT IN {1}");
        addTemplate(ComparisionOp.BETWEEN, "{0} BETWEEN {1} AND {2}");
        addTemplate(ComparisionOp.GOE, "{0} >= {1}");
        addTemplate(ComparisionOp.GT, "{0} > {1}");
        addTemplate(ComparisionOp.LOE, "{0} <= {1}");
        addTemplate(ComparisionOp.LT, "{0} < {1}");
    }

    @Override
    public void handle(ComparisionOp operator, Operation<?> expr, Visitor<VisitorContext> visitor, VisitorContext context) {

        if(operator == ComparisionOp.BETWEEN) {
            handleBetween(operator, expr, visitor, context);
            return;
        }

        Expression<?> left = expr.getArg(0);
        Expression<?> right = expr.getArg(1);

        VisitorContext leftContext = new VisitorContext(context);
        VisitorContext rightContext = new VisitorContext(context);

        // 左辺の評価
        invoke(operator, left, visitor, leftContext);

        /*
         * 比較演算子の場合、左変=プロパティパス、右辺=定数で構成される場合、
         * 定数をプロパティの変換規則に従い変換する。
         */
        if(left instanceof PropertyPath && right instanceof Constant) {
            visitConstantWithPropertyPath((PropertyPath<?>)left, (Constant<?>)right, rightContext);
        } else {
            invoke(operator, right, visitor, rightContext);
        }

        // 評価した結果を親のコンテキストに追加する
        String sql = formatWithTemplate(operator, leftContext.getCriteria(), rightContext.getCriteria());
        context.appendSql(sql);

        context.addParamValues(leftContext.getParamValues());
        context.addParamValues(rightContext.getParamValues());

    }

    private void handleBetween(ComparisionOp operator, Operation<?> expr, Visitor<VisitorContext> visitor, VisitorContext context) {

        Expression<?> left = expr.getArg(0);
        Expression<?> right1 = expr.getArg(1);
        Expression<?> right2 = expr.getArg(1);

        VisitorContext leftContext = new VisitorContext(context);
        VisitorContext rightContext1 = new VisitorContext(context);
        VisitorContext rightContext2 = new VisitorContext(context);

        // 左辺の評価
        invoke(operator, left, visitor, leftContext);

        /*
         * 比較演算子の場合、左変=プロパティパス、右辺=定数で構成される場合、
         * 定数をプロパティの変換規則に従い変換する。
         */
        if(left instanceof PropertyPath && right1 instanceof Constant) {
            visitConstantWithPropertyPath((PropertyPath<?>)left, (Constant<?>)right1, rightContext1);
        } else {
            invoke(operator, right1, visitor, rightContext1);
        }

        if(left instanceof PropertyPath && right2 instanceof Constant) {
            visitConstantWithPropertyPath((PropertyPath<?>)left, (Constant<?>)right2, rightContext2);
        } else {
            invoke(operator, right2, visitor, rightContext2);
        }

        // 評価した結果を親のコンテキストに追加する
        String sql = formatWithTemplate(operator, leftContext.getCriteria(), rightContext1.getCriteria(), rightContext2.getCriteria());
        context.appendSql(sql);

        context.addParamValues(leftContext.getParamValues());
        context.addParamValues(rightContext1.getParamValues());
        context.addParamValues(rightContext2.getParamValues());

    }

}

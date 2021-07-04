package com.github.mygreen.sqlmapper.core.where.metamodel;

import com.github.mygreen.sqlmapper.metamodel.PropertyPath;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operator.ArithmeticOp;

/**
 * 算術演算子({@link ArithmeticOp})に対する処理を定義します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ArithmeticOpHandler extends OperationHandler<ArithmeticOp> {

    @Override
    protected void init() {
        // 算術演算子
        addTemplate(ArithmeticOp.MULT, "{0} * {1}");
        addTemplate(ArithmeticOp.DIV, "{0} / {1}");
        addTemplate(ArithmeticOp.MOD, "{0} % {1}");
        addTemplate(ArithmeticOp.ADD, "{0} + {1}");
        addTemplate(ArithmeticOp.SUB, "{0} - {1}");

    }

    @Override
    public void handle(ArithmeticOp operator, Operation<?> expr, Visitor<VisitorContext> visitor, VisitorContext context) {

        Expression<?> left = expr.getArg(0);
        Expression<?> right = expr.getArg(1);

        VisitorContext leftContext = new VisitorContext(context);
        VisitorContext rightContext = new VisitorContext(context);

        // 左辺の評価
        invoke(operator, left, visitor, leftContext);

        /*
         * 算術演算子の場合、左変=プロパティパス、右辺=定数で構成される場合、
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
}

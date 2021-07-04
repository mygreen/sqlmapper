package com.github.mygreen.sqlmapper.core.where.metamodel;

import com.github.mygreen.sqlmapper.core.util.QueryUtils;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operator.LikeOp;

/**
 * LIKE演算子({@link LikeOp})に対する処理を定義します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class LikeOpHandler extends OperationHandler<LikeOp>{

    @Override
    protected void init() {
        // 何もしない
    }

    @Override
    public void handle(LikeOp operator, Operation<?> expr, Visitor<VisitorContext> visitor, VisitorContext context) {

        Expression<?> left = expr.getArg(0);
        Expression<?> right = expr.getArg(1);

        if(right instanceof Constant) {
            String text = ((Constant<?>)right).getValue().toString();
            switch(operator) {
                case CONTAINS:
                    text = "%" + QueryUtils.escapeLike(text) + "%";
                    break;
                case STARTS:
                    text = QueryUtils.escapeLike(text) + "%";
                    break;
                case ENDS:
                    text = "%" + QueryUtils.escapeLike(text);
                    break;
                case LIKE:
                default:
                    // 何もしない
            }

            invoke(operator, left, visitor, context);
            context.appendSql(" like ?");
            context.addParamValue(text);

        } else {
            // 定数以外はただのLIKEとして扱う
            invoke(operator, left, visitor, context);
            context.appendSql(" like ");
            invoke(operator, right, visitor, context);
        }
    }

}

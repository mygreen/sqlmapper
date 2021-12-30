package com.github.mygreen.sqlmapper.core.where.metamodel;

import java.util.Optional;

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

    /**
     * デフォルトのLIKEエスケープ文字
     */
    private static char DEFAULT_LIKE_ESCAPE = '\\';

    @Override
    protected void init() {
        // 何もしない
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(LikeOp operator, Operation<?> expr, Visitor<VisitorContext> visitor, VisitorContext context) {

        Expression<?> left = expr.getArg(0);
        Expression<?> right = expr.getArg(1);
        Optional<Expression<?>> escapeExp = expr.getOptArg(2);
        char escape = escapeExp.map(e -> ((Constant<Character>)e).getValue()).orElse(DEFAULT_LIKE_ESCAPE);

        if(right instanceof Constant) {
            String text = ((Constant<?>)right).getValue().toString();

            switch(operator) {
                case CONTAINS:
                    text = "%" + QueryUtils.escapeLike(text, escape) + "%";
                    break;
                case STARTS:
                    text = QueryUtils.escapeLike(text, escape) + "%";
                    break;
                case ENDS:
                    text = "%" + QueryUtils.escapeLike(text, escape);
                    break;
                case LIKE:
                default:
                    // 何もしない
            }

            invoke(operator, left, visitor, context);
            context.appendSql(" like ?");
            if(escapeExp.isPresent()) {
                context.appendSql(" escape '" + escape + "'");
            }
            context.addParamValue(text);

        } else {
            // 定数以外はただのLIKEとして扱う
            invoke(operator, left, visitor, context);
            context.appendSql(" like ");
            if(escapeExp.isPresent()) {
                context.appendSql(" escape '" + escape + "'");
            }
            invoke(operator, right, visitor, context);
        }
    }

}

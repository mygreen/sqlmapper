package com.github.mygreen.sqlmapper.core.where.metamodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.query.IllegalQueryException;
import com.github.mygreen.sqlmapper.core.util.QueryUtils;
import com.github.mygreen.sqlmapper.metamodel.Path;
import com.github.mygreen.sqlmapper.metamodel.PathMeta;
import com.github.mygreen.sqlmapper.metamodel.PathType;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.expression.SubQueryExpression;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operator.BooleanOp;
import com.github.mygreen.sqlmapper.metamodel.operator.ComparisionOp;
import com.github.mygreen.sqlmapper.metamodel.operator.FuncOp;
import com.github.mygreen.sqlmapper.metamodel.operator.LikeOp;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;
import com.github.mygreen.sqlmapper.metamodel.operator.UnaryOp;

/**
 * メタモデルの式ノードを巡回するVisitorです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ExpressionVisitor implements Visitor<VisitorContext> {

    private Map<Class<?>, OperationHandler<? extends Operator>> operationHandlerMap;
    {
        this.operationHandlerMap = new HashMap<>();
        operationHandlerMap.put(BooleanOp.class, new BooleanOpHandler());
        operationHandlerMap.put(UnaryOp.class, new UnaryOpHandler());
        operationHandlerMap.put(ComparisionOp.class, new ComparisionOpHandler());
        operationHandlerMap.put(LikeOp.class, new LikeOpHandler());
        operationHandlerMap.put(FuncOp.class, new FuncOpHandler());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void visit(Operation<?> expr, VisitorContext context) {
        final Operator operator = expr.getOperator();

        OperationHandler handler = operationHandlerMap.get(operator.getClass());
        handler.handle(operator, expr, this, context);

    }

    @Override
    public void visit(Constant<?> expr, VisitorContext context) {
        // 値はプレースホルダーを追加
        if(expr.isExpandable()) {
            // IN句などの展開可能な複数要素の場合
            Collection<?> values = (Collection<?>)expr.getValue();
            context.addParamValues(values);
            context.appendSql("(")
                .append(QueryUtils.repeat("?", ",", values.size()))
                .append(")");
        } else {
            context.addParamValue(expr.getValue());
            context.appendSql("?");

        }

    }


    @Override
    public void visit(Path<?> expr, VisitorContext context) {

        PathMeta pathMeta = expr.getPathMeta();
        if(pathMeta.getType() == PathType.PROPERTY) {
            String propertyName = pathMeta.getElement();
            Optional<PropertyMeta> propertyMeta = context.getEntityMeta().getPropertyMeta(propertyName);
            if(propertyMeta.isEmpty()) {
                throw new IllegalQueryException("unknwon property : " + propertyName);
            }

            // TODO: Embeddedのネストした場合を考慮する
            final String tableName = context.getTableNameResolver().getTableAlias(expr.getPathMeta().getParent());
            final String columnName;
            // SQL - カラム名を追加
            if(tableName != null) {
                columnName = tableName + "." + propertyMeta.get().getColumnMeta().getName();
            } else {
                columnName = propertyMeta.get().getColumnMeta().getName();;
            }
            context.appendSql(columnName);

        } else {
            throw new IllegalArgumentException("not support pathType=" + pathMeta.getType());
        }

    }

    @Override
    public void visit(SubQueryExpression<?> expr, VisitorContext context) {
        // TODO 自動生成されたメソッド・スタブ

    }




}

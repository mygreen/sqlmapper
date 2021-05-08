package com.github.mygreen.sqlmapper.core.where.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.query.FromClause;
import com.github.mygreen.sqlmapper.core.query.IllegalQueryException;
import com.github.mygreen.sqlmapper.core.query.OrderByClause;
import com.github.mygreen.sqlmapper.core.query.SelectClause;
import com.github.mygreen.sqlmapper.core.query.TableNameResolver;
import com.github.mygreen.sqlmapper.core.query.WhereClause;
import com.github.mygreen.sqlmapper.core.util.QueryUtils;
import com.github.mygreen.sqlmapper.metamodel.OrderSpecifier;
import com.github.mygreen.sqlmapper.metamodel.Path;
import com.github.mygreen.sqlmapper.metamodel.PathMeta;
import com.github.mygreen.sqlmapper.metamodel.PathType;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.expression.SubQueryExpression;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operation.QueryMeta;
import com.github.mygreen.sqlmapper.metamodel.operator.BooleanOp;
import com.github.mygreen.sqlmapper.metamodel.operator.ComparisionOp;
import com.github.mygreen.sqlmapper.metamodel.operator.FunctionOp;
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
        operationHandlerMap.put(FunctionOp.class, new FuncOpHandler());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void visit(final Operation<?> expr, final VisitorContext context) {
        final Operator operator = expr.getOperator();

        OperationHandler handler = operationHandlerMap.get(operator.getClass());
        handler.handle(operator, expr, this, context);

    }

    @Override
    public void visit(final Constant<?> expr, final VisitorContext context) {
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
    public void visit(final Path<?> expr, final VisitorContext context) {

        final PathMeta pathMeta = expr.getPathMeta();
        if(pathMeta.getType() == PathType.PROPERTY) {
            Class<?> parentClassType = pathMeta.getParent().getType();
            String propertyName = pathMeta.getElement();
            Optional<PropertyMeta> propertyMeta = context.getEntityMetaMap().get(parentClassType).getPropertyMeta(propertyName);
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
    public void visit(final SubQueryExpression<?> expr, final VisitorContext context) {
        final QueryMeta queryMeta = expr.getQueryMeta();
        final Dialect dialect = context.getDialect();
        final TableNameResolver tableNameResolver = context.getTableNameResolver();

        // テーブル名の組み立て
        //TODO: テーブル名は新規に割り当てる必要があるかどうか。
        final FromClause fromClause = new FromClause();
        tableNameResolver.prepareTableAlias(queryMeta.getEntityPath());
        EntityMeta entityMeta = context.getEntityMetaFactory().create(queryMeta.getEntityPath().getType());
        fromClause.addSql(entityMeta.getTableMeta().getFullName(), tableNameResolver.getTableAlias(queryMeta.getEntityPath()));

        // 抽出カラムの組み立て
        final SelectClause selectClause = new SelectClause();
        for(PropertyMeta propertyMeta : entityMeta.getAllColumnPropertyMeta()) {
            final String propertyName = propertyMeta.getName();
            final PropertyPath<?> propertyPath = queryMeta.getEntityPath().getPropertyPath(propertyName);

            if(propertyMeta.isTransient()) {
                continue;
            }

            if(!queryMeta.getIncludesProperties().isEmpty()
                    && !queryMeta.getIncludesProperties().contains(propertyPath)) {
                continue;
            }

            String tableAlias = tableNameResolver.getTableAlias(queryMeta.getEntityPath());
            selectClause.addSql(tableAlias, propertyMeta.getColumnMeta().getName());
        }

        // 条件の組み立て
        final WhereClause whereClause = new WhereClause();
        final List<Object> paramValues = new ArrayList<>();
        if(queryMeta.getWhere() != null) {
            MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(context.getEntityMetaMap(), context.getDialect(),
                    context.getEntityMetaFactory(), tableNameResolver);
            visitor.visit(new MetamodelWhere(queryMeta.getWhere()));

            whereClause.addSql(visitor.getCriteria());
            paramValues.addAll(visitor.getParamValues());
        }

        // 並び順の組み立て
        final OrderByClause orderByClause = new OrderByClause();
        for(OrderSpecifier order : queryMeta.getOrders()) {
            String propertyName = order.getPath().getPathMeta().getElement();
            Optional<PropertyMeta> propertyMeta = entityMeta.getPropertyMeta(propertyName);

            String tableAlias = tableNameResolver.getTableAlias(order.getPath().getPathMeta().getParent());
            if(!StringUtils.hasLength(tableAlias)) {
                //TODO: 例外処理

            }

            propertyMeta.ifPresent(p -> {
                String orderBy = String.format("%s.%s %s", tableAlias, p.getColumnMeta().getName(), order.getOrder().name());
                orderByClause.addSql(orderBy);
            });
        }

        String sql = "select "
                + selectClause.toSql()
                + fromClause.toSql()
                + whereClause.toSql()
                + orderByClause.toSql();

        // limit句の組み立て
        if(queryMeta.getLimit() > 0 || queryMeta.getLimit() == 0 && queryMeta.getOffset() > 0) {
            sql = dialect.convertLimitSql(sql, queryMeta.getOffset(), queryMeta.getLimit());
        }

        // 組み立てたSQLとパラメータを格納する
        context.addParamValues(paramValues);
        context.appendSql(sql);
    }

}

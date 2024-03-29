package com.github.mygreen.sqlmapper.metamodel.support;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mygreen.sqlmapper.metamodel.OrderSpecifier;
import com.github.mygreen.sqlmapper.metamodel.Path;
import com.github.mygreen.sqlmapper.metamodel.PathMeta;
import com.github.mygreen.sqlmapper.metamodel.PathType;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.expression.SubQueryExpression;
import com.github.mygreen.sqlmapper.metamodel.operation.CustomFunctionOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operation.SubQueryMeta;
import com.github.mygreen.sqlmapper.metamodel.operator.ArithmeticOp;
import com.github.mygreen.sqlmapper.metamodel.operator.BooleanOp;
import com.github.mygreen.sqlmapper.metamodel.operator.ComparisionOp;
import com.github.mygreen.sqlmapper.metamodel.operator.FunctionOp;
import com.github.mygreen.sqlmapper.metamodel.operator.LikeOp;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;
import com.github.mygreen.sqlmapper.metamodel.operator.UnaryOp;
import com.github.mygreen.sqlmapper.metamodel.support.SqlFunctionParser.Token;
import com.github.mygreen.sqlmapper.metamodel.support.SqlFunctionTokenizer.TokenType;

/**
 * 式を文字列として評価するためのデバッグ用のVisitor。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class DebugVisitor implements Visitor<DebugVisitorContext>{

    private static final DateTimeFormatter FORMATTER_LOCAL_DATETIME = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_LOCAL_DATE = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private static final DateTimeFormatter FORMATTER_LOCAL_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 演算子に対する式テンプレートのマップ
     */
    private Map<Operator, String> operationTemplateMap = new HashMap<>();

    public DebugVisitor() {
        initOperationTemplate();
    }

    protected void initOperationTemplate() {

        // BooleanOp
        operationTemplateMap.put(BooleanOp.AND, "{0} and {1}");
        operationTemplateMap.put(BooleanOp.OR, "{0} or {1}");

        // UnaryOp
        operationTemplateMap.put(UnaryOp.NOT, "not {0}");
        operationTemplateMap.put(UnaryOp.IS_NULL, "{0} is null");
        operationTemplateMap.put(UnaryOp.IS_NOT_NULL, "{0} is not null");
        operationTemplateMap.put(UnaryOp.EXISTS, "exists {0}");
        operationTemplateMap.put(UnaryOp.NOT_EXISTS, "not exists {0}");

        // ComparisionOp
        operationTemplateMap.put(ComparisionOp.EQ, "{0} = {1}");
        operationTemplateMap.put(ComparisionOp.NE, "{0} <> {1}");
        operationTemplateMap.put(ComparisionOp.IN, "{0} in {1}");
        operationTemplateMap.put(ComparisionOp.NOT_IN, "{0} not in {1}");
        operationTemplateMap.put(ComparisionOp.BETWEEN, "{0} between {1} and {2}");
        operationTemplateMap.put(ComparisionOp.GOE, "{0} >= {1}");
        operationTemplateMap.put(ComparisionOp.GT, "{0} > {1}");
        operationTemplateMap.put(ComparisionOp.LOE, "{0} <= {1}");
        operationTemplateMap.put(ComparisionOp.LT, "{0} < {1}");

        // ArithmeticOp
        operationTemplateMap.put(ArithmeticOp.MULT, "{0} * {1}");
        operationTemplateMap.put(ArithmeticOp.DIV, "{0} / {1}");
        operationTemplateMap.put(ArithmeticOp.MOD, "mod({0}, {1})");
        operationTemplateMap.put(ArithmeticOp.ADD, "{0} + {1}");
        operationTemplateMap.put(ArithmeticOp.SUB, "{0} - {1}");

        // FunctionOp
        operationTemplateMap.put(FunctionOp.LOWER, "lower({0})");
        operationTemplateMap.put(FunctionOp.UPPER, "upper({0})");
        operationTemplateMap.put(FunctionOp.CONCAT, "concat({0}, {1})");
//        operationTemplateMap.put(FunctionOp.CURRENT_DATE, "current_date");
//        operationTemplateMap.put(FunctionOp.CURRENT_TIME, "current_time");
//        operationTemplateMap.put(FunctionOp.CURRENT_TIMESTAMP, "current_timestamp");
//        operationTemplateMap.put(FunctionOp.CUSTOM, "custom()");

        // LikeOp
        operationTemplateMap.put(LikeOp.LIKE, "{0} like {1}");
        // 関数として扱う
        operationTemplateMap.put(LikeOp.CONTAINS, "contains({0}, {1})");
        operationTemplateMap.put(LikeOp.STARTS, "starts({0}, {1})");
        operationTemplateMap.put(LikeOp.ENDS, "ends({0}, {1})");

    }

    @Override
    public void visit(final Operation<?> expr, final DebugVisitorContext context) {

        final Operator operator = expr.getOperator();
        final String template = operationTemplateMap.get(operator);
        if(template != null) {
            // 引数を評価します。
            List<Object> evals = new ArrayList<>();
            for(Expression<?> arg : expr.getArgs()) {
                DebugVisitorContext argContext =  new DebugVisitorContext();
                invoke(operator, arg, argContext);
                evals.add(argContext.getCriteria());
            }

            String formatExpr = MessageFormat.format(template, evals.toArray(new Object[evals.size()]));
            context.append(formatExpr);

        } else if(operator == FunctionOp.CURRENT_DATE
                || operator == FunctionOp.CURRENT_TIME
                || operator == FunctionOp.CURRENT_TIMESTAMP) {
            context.append(operator.name().toLowerCase());
            // 精度が存在するとき評価する
            if(!expr.getArgs().isEmpty()) {
                context.append("(");
                invoke(operator, expr.getArg(0), context);
                context.append(")");

            }

        } else if(operator == FunctionOp.CUSTOM) {

            // 左辺の評価
            Expression<?> left = expr.getArg(0);
            DebugVisitorContext leftContext = new DebugVisitorContext();
            invoke(operator, left, leftContext);

            // トークンを置換していく
            CustomFunctionOperation op = (CustomFunctionOperation) expr.getArg(1);
            @SuppressWarnings("unchecked")
            List<Token> tokens = op.getTokens();
            for(Token token : tokens) {
                if(token.type == TokenType.SQL) {
                    context.append(token.value);

                } else if(token.type == TokenType.LEFT_VARIABLE) {
                    context.append(leftContext.getCriteria());

                } else if(token.type == TokenType.BIND_VARIABLE) {
                    int varIndex = token.bindBariableIndex;
                    Expression<?> arg = op.getArg(varIndex);

                    DebugVisitorContext argContext = new DebugVisitorContext();
                    invoke(operator, arg, argContext);

                    context.append(argContext.getCriteria());

                } else {
                    // unknown token
                }

            }

        }
    }

    @Override
    public void visit(final Constant<?> expr, final DebugVisitorContext context) {

        final Object constant = expr.getValue();

        if(constant == null) {
            context.append("null");

        } else if(expr.isExpandable()) {
            context.append("(");
            StringBuilder joined = new StringBuilder();
            for(Object value : (Collection<?>)constant) {
                if(joined.length() > 0) {
                    joined.append(", ");
                }
                joined.append(formatConstantValue(value));
            }
            context.append(joined.toString())
                .append(")");

        } else {
            context.append(formatConstantValue(constant));
        }

    }

    /**
     * 定数の値をフォーマットします。
     * @param value フォーマット対象の値
     * @return フォーマットした値
     */
    private String formatConstantValue(final Object value) {
        if(value == null) {
            return "null";
        }

        if(value instanceof CharSequence) {
            return "'" + value.toString() + "'";

        } else if(value instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
        } else if(value instanceof java.sql.Date) {
            return new SimpleDateFormat("yyyy-MM-dd").format(value);
        } else if(value instanceof Time) {
            return new SimpleDateFormat("HH:mm:ss").format(value);
        } else if(value instanceof Timestamp) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(value);
        } else if(value instanceof LocalDateTime) {
            return FORMATTER_LOCAL_DATETIME.format((LocalDateTime)value);
        } else if(value instanceof LocalDate) {
            return FORMATTER_LOCAL_DATE.format((LocalDate)value);
        } else if(value instanceof LocalTime) {
            return FORMATTER_LOCAL_TIME.format((LocalTime)value);
        }

        return value.toString();
    }

    @Override
    public void visit(final Path<?> expr, final DebugVisitorContext context) {

        PathMeta pathMeta = expr.getPathMeta();
        String pathName = pathMeta.getElement();
        Path<?> parent = null;

        // 親をたどりパス名を追加していく。
        // <親のパス>.<子のパス>
        do {
            parent = pathMeta.getParent();
            if(parent == null) {
                break;
            }
            pathMeta = parent.getPathMeta();
            // 親の名前を先頭に追加していく。
            pathName = pathMeta.getElement() + "." + pathName;
        } while(parent.getPathMeta().getType() != PathType.ROOT);

        context.append(pathName);

    }

    @Override
    public void visit(final SubQueryExpression<?> expr, final DebugVisitorContext context) {

        final SubQueryMeta queryMeta = expr.getQueryMeta();

        context.append("select");

        // 抽出カラムの組み立て
        if(!queryMeta.getIncludesProperties().isEmpty()) {
            StringBuilder columnQuery = new StringBuilder();
            for(PropertyPath<?> propertyPath : queryMeta.getIncludesProperties()) {
                if(columnQuery.length() > 0) {
                    columnQuery.append(", ");
                }

                // プロパティのパスの評価
                DebugVisitorContext propertyPathContext = new DebugVisitorContext();
                propertyPath.accept(this, propertyPathContext);
                columnQuery.append(propertyPathContext.getCriteria());
            }
            context.append(columnQuery.toString());
        } else {
            context.append(" *");
        }

        // from句の組み立て
        context.append(" from ").append(queryMeta.getEntityPath().getPathMeta().getElement());

        // where句の組み立て
        if(queryMeta.getWhere() != null) {
            // 条件式の評価
            DebugVisitorContext whereContext = new DebugVisitorContext();
            queryMeta.getWhere().accept(this, whereContext);

            context.append(" whrere ")
                .append(whereContext.getCriteria());
        }

        // 並び順の組み立て
        if(!queryMeta.getOrders().isEmpty()) {
            StringBuilder orderQuery = new StringBuilder();
            for(OrderSpecifier order : queryMeta.getOrders()) {
                if(orderQuery.length() > 0) {
                    orderQuery.append(", ");
                }

                // 並び順のパスを評価する
                DebugVisitorContext orderContext = new DebugVisitorContext();
                order.getPath().accept(this, orderContext);
                orderQuery.append(orderContext.getCriteria())
                    .append(" ")
                    .append(order.getOrder().name());
            }
            context.append(" order by ")
                .append(orderQuery.toString());
        }

        // limit句の組み立て
        if(queryMeta.getLimit() > 0 || queryMeta.getLimit() == 0 && queryMeta.getOffset() > 0) {
            if (queryMeta.getOffset() > 0) {
                context.append(" limit ")
                    .append(queryMeta.getLimit())
                    .append(" offset ")
                    .append(queryMeta.getOffset());
            } else {
                context.append(" limit ")
                    .append(queryMeta.getLimit());
            }
        }

    }

    protected void invoke(final Operator parentOperator, final Expression<?> expr, final DebugVisitorContext context) {

        if(expr instanceof Operation) {
            Operation<?> operation = (Operation<?>)expr;
            // /子ノードが演算子の場合、括弧で囲むか判定する。
            if(OperationUtils.isEnclosedParenthesis(parentOperator, operation.getOperator())) {
                context.append("(");
                visit(operation, context);
                context.append(")");
            } else {
                visit(operation, context);
            }

        } else if(expr instanceof Constant) {
            visit((Constant<?>)expr, context);
        } else if (expr instanceof Path) {
            visit((Path<?>)expr, context);
        } else if(expr instanceof SubQueryExpression) {
            context.append("(");
            visit((SubQueryExpression<?>)expr, context);
            context.append(")");
        } else {
            throw new IllegalArgumentException("not support Expression instance of " + expr.getClass());
        }

    }
}

package com.github.mygreen.sqlmapper.core.where.metamodel;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.query.IllegalQueryException;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.util.QueryUtils;
import com.github.mygreen.sqlmapper.metamodel.Path;
import com.github.mygreen.sqlmapper.metamodel.PathType;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.expression.SubQueryExpression;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;
import com.github.mygreen.sqlmapper.metamodel.support.OperationUtils;

/**
 * 演算子に対する処理を行うためのテンプレートクラス。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象の演算子
 */
public abstract class OperationHandler<T extends Operator> {

    /**
     * 演算子に対するテンプレートのマップ
     * マップの値ととなるテンプレートは{@link MessageFormat}の形式。
     */
    protected Map<T, String> templateMap = new HashMap<>();

    /**
     * 初期化処理
     */
    protected abstract void init();

    /**
     * 演算子に対する処理を行います。
     * @param operator 演算子
     * @param expr 演算子と非演算子を含む処理対象の式の情報
     * @param visitor Visitor
     * @param context このンテキスト
     */
    public abstract void handle(T operator, Operation<?> expr, Visitor<VisitorContext> visitor, VisitorContext context);

    /**
     * テンプレートを追加します。
     * @param op 演算子
     * @param template テンプレート（{@link MessageFormat}の形式。）
     * @return 既に演算子に対するテンプレートが追加されている場合は、古いテンプレートの値を返します。
     */
    public String addTemplate(T op, String template) {
        return templateMap.put(op, template);
    }

    /**
     * 演算子に対応するテンプレートを取得します。
     * @param op 演算子
     * @return 対応するテンプレートが存在しない場合は、nullを返します。
     */
    public String getTemplate(T op) {
        return templateMap.get(op);
    }

    /**
     * テンプレートを使用してフォーマットします。
     * @param op 演算子
     * @param args 引数
     * @return フォーマットした値
     */
    public String formatWithTemplate(T op, Object... args) {
        String template = templateMap.get(op);
        return MessageFormat.format(template, args);
    }

    /**
     * 式がプロパティパスかどうか判定します。
     * @param exp 式
     * @return プロパティパスの場合、{@literal true}を返します。
     */
    protected boolean isPropertyPath(Expression<?> exp) {

        if(!(exp instanceof Path)) {
            return false;
        }

        Path<?> path = (Path<?>) exp;
        return path.getPathMeta().getType() == PathType.PROPERTY;

    }

    /**
     * プロパティが確定しているのとき定数の処理。
     * @param propertyPath プロパティパス
     * @param expr 定数
     * @param context コンテキスト
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void visitConstantWithPropertyPath(Path<?> propertyPath, Constant<?> expr, VisitorContext context) {

        assert propertyPath.getPathMeta().getType() == PathType.PROPERTY;

        Path<?> rootPath = propertyPath.getPathMeta().findRootPath();
        Class<?> rootClassType = rootPath.getType();
        String propertyName = propertyPath.getPathMeta().getElement();
        Optional<PropertyMeta> propertyMeta = context.getEntityMetaMap().get(rootClassType).findPropertyMeta(propertyName);
        if(propertyMeta.isEmpty()) {
            throw new IllegalQueryException("unknwon property : " + propertyName);
        }

        ValueType valueType = propertyMeta.get().getValueType();

        // 値はプレースホルダーを追加
        if(expr.isExpandable()) {
            // 展開可能な複数の要素の場合
            Collection<?> values = (Collection<?>)expr.getValue();
            for(Object value : values) {
                context.addParamValue(valueType.getSqlParameterValue(value));
            }
            context.appendSql("(")
                .append(QueryUtils.repeat("?", ", ", values.size()))
                .append(")");
        } else {
            context.addParamValue(valueType.getSqlParameterValue(expr.getValue()));
            context.appendSql("?");
        }

    }

    /**
     * 各処理に振り分ける
     * @param parentOperator 親ノードの演算子
     * @param expr 評価対象の式
     * @param visitor Visitor
     * @param context コンテキスト
     */
    protected void invoke(Operator parentOperator, Expression<?> expr, Visitor<VisitorContext> visitor, VisitorContext context) {

        if(expr instanceof Operation) {
            Operation<?> operation = (Operation<?>)expr;
            // /子ノードが演算子の場合、括弧で囲むか判定する。
            if(OperationUtils.isEnclosedParenthesis(parentOperator, operation.getOperator())) {
                context.appendSql("(");
                visitor.visit(operation, context);
                context.appendSql(")");
            } else {
                visitor.visit(operation, context);
            }

        } else if(expr instanceof Constant) {
            visitor.visit((Constant<?>)expr, context);
        } else if (expr instanceof Path) {
            visitor.visit((Path<?>)expr, context);
        } else if(expr instanceof SubQueryExpression) {
            context.appendSql("(");
            visitor.visit((SubQueryExpression<?>)expr, context);
            context.appendSql(")");
        } else {
            throw new IllegalArgumentException("not support Expression instance of " + expr.getClass());
        }

    }

}

package com.github.mygreen.sqlmapper.core.where.metamodel;

import java.util.List;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Expression;

/**
 * SQL関数を処理するためのインタフェース。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface SqlFunction {

    /**
     * SQL関数を処理します。
     *
     * @param args 関数の引数。
     * @param visitor 式ノードを巡回するためのVisitor。
     * @param context 組み立てたSQL情報を保持します。
     * @param evaluator 式を評価する処理
     */
    void handle(List<Expression<?>> args, Visitor<VisitorContext> visitor, VisitorContext context,
            ExpressionEvaluator evaluator);

}

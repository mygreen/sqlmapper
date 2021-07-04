package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.expression.SubQueryExpression;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;

/**
 * 式を巡回するビジターのインタフェース。
 *
 *
 * @author T.TSUCHIE
 * @param <C> コンテキストのクラスタイプ
 *
 */
public interface Visitor<C> {

    /**
     * 演算子を処理します。
     * @param expr 演算子の式
     * @param context コンテキスト
     */
    void visit(Operation<?> expr, C context);

    /**
     * 定数を処理します。
     * @param expr 定数の式
     * @param context コンテキスト
     */
    void visit(Constant<?> expr, C context);

    /**
     * パスを処理します。
     * @param expr パスの式
     * @param context コンテキスト
     */
    void visit(Path<?> expr, C context);

    /**
     * サブクエリを処理します。
     * @param expr サブクエリの式
     * @param context コンテキスト
     */
    void visit(SubQueryExpression<?> expr, C context);
}

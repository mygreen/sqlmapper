package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.expression.SubQueryExpression;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;

/**
 * 式を巡回するビジター
 *
 *
 * @author T.TSUCHIE
 * @param <C> コンテキストのクラスタイプ
 *
 */
public interface Visitor<C> {

    void visit(Constant<?> expr, C context);

    void visit(Operation<?> expr, C context);

    void visit(Path<?> expr, C context);

    void visit(SubQueryExpression<?> expr, C context);
}

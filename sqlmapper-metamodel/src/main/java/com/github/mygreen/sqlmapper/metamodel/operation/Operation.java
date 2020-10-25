package com.github.mygreen.sqlmapper.metamodel.operation;

import java.util.List;

import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

/**
 * 演算子を表す
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T>
 */
public interface Operation<T> {

    /**
     * 演算子の種別を取得します。
     * @return 演算子の種別
     */
    Operator getOperator();

    /**
     * インデックスを指定して引数を取得します。
     * @param index 0から始まるインデックス。
     * @return インデックスで指定した式。
     */
    Expression<?> getArg(int index);

    /**
     * 演算子の引数を取得します。
     * @return 引数の一覧
     */
    List<Expression<?>> getArgs();


}

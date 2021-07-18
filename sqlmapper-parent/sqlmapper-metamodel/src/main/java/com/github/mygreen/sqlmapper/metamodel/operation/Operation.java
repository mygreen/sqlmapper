package com.github.mygreen.sqlmapper.metamodel.operation;

import java.util.List;

import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

/**
 * 式の演算子と引数を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 演算対象のクラスタイプ
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
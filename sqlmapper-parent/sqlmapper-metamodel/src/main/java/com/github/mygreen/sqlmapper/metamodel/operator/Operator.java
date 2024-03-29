package com.github.mygreen.sqlmapper.metamodel.operator;

/**
 * 演算子を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface Operator {

    /**
     * 演算子の名称を取得します。
     * @return 演算子の名称
     */
    String name();

    /**
     * 演算子の結果のタイプを取得します。
     * @return 演算子の結果のタイプ
     */
    Class<?> getResultType();

    /**
     * 演算子の優先度。
     * @return 値が小さいほど優先度が高い。
     */
    int getPriority();

}

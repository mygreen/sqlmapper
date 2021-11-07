package com.github.mygreen.sqlmapper.metamodel.operator;

import lombok.Getter;

/**
 * 関数を表現する演算子
 *
 * @author T.TSUCHIE
 *
 */
public enum FunctionOp implements Operator {

    // String
    LOWER(String.class, -1),
    UPPER(String.class, -1),
    CONCAT(String.class, -1),

    // Date/Time
    CURRENT_DATE(Comparable.class, -1),
    CURRENT_TIME(Comparable.class, -1),
    CURRENT_TIMESTAMP(Comparable.class, -1)

    ;

    /**
     * 演算子の結果のタイプ
     */
    @Getter
    private final Class<?> resultType;

    /**
     * 演算子の優先度
     */
    @Getter
    private final int priority;

    private FunctionOp(Class<?> resultType, int priority) {
        this.resultType = resultType;
        this.priority = priority;
    }
}

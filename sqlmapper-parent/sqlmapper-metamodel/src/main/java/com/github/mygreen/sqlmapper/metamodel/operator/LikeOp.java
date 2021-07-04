package com.github.mygreen.sqlmapper.metamodel.operator;

import lombok.Getter;

/**
 * LIKE演算子
 *
 *
 * @author T.TSUCHIE
 *
 */
public enum LikeOp implements Operator {

    LIKE(Boolean.class, 40),
    CONTAINS(Boolean.class, 40),
    STARTS(Boolean.class, 40),
    ENDS(Boolean.class, 40)

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

    private LikeOp(Class<?> resultType, int priority) {
        this.resultType = resultType;
        this.priority = priority;
    }
}

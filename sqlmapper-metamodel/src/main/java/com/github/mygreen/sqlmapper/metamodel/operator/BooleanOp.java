package com.github.mygreen.sqlmapper.metamodel.operator;

import lombok.Getter;

/**
 * 論理演算子。
 *
 *
 * @author T.TSUCHIE
 *
 */
public enum BooleanOp implements Operator {

    AND(Boolean.class, 80),
    OR(Boolean.class, 90)
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

    private BooleanOp(Class<?> resultType, int priority) {
        this.resultType = resultType;
        this.priority = priority;
    }

}

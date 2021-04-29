package com.github.mygreen.sqlmapper.metamodel.operator;

import lombok.Getter;

/**
 * 単項演算子
 *
 *
 * @author T.TSUCHIE
 *
 */
public enum UnaryOp implements Operator {

    NOT(Boolean.class, 70),

    // NULL比較
    IS_NULL(Boolean.class, 50),
    IS_NOT_NULL(Boolean.class, 50),

    EXISTS(Boolean.class, 60),
    NOT_EXISTS(Boolean.class, 60)
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

    private UnaryOp(Class<?> resultType, int priority) {
        this.resultType = resultType;
        this.priority = priority;
    }
}

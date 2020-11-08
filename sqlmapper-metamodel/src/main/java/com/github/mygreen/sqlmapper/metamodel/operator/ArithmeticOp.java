package com.github.mygreen.sqlmapper.metamodel.operator;

import lombok.Getter;

/**
 * 算術演算子
 *
 *
 * @author T.TSUCHIE
 *
 */
public enum ArithmeticOp implements Operator {

    MULT(Number.class, 20),
    DIV(Number.class, 20),
    MOD(Number.class, 20),
    ADD(Number.class, 30),
    SUB(Number.class, 30),

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

    private ArithmeticOp(Class<?> resultType, int priority) {
        this.resultType = resultType;
        this.priority = priority;
    }
}

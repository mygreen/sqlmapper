package com.github.mygreen.sqlmapper.metamodel.operator;

import lombok.Getter;

/**
 * 比較演算子
 * <p>優先度は全て同じ。
 *
 * @author T.TSUCHIE
 *
 */
public enum ComparisionOp implements Operator {

    IN(Boolean.class, 40),
    NOT_IN(Boolean.class, 40),
    BETWEEN(Boolean.class, 60),

    // 比較演算子
    EQ(Boolean.class, 40),
    NE(Boolean.class, 40),
    GOE(Boolean.class, 40),
    GT(Boolean.class, 40),
    LOE(Boolean.class, 40),
    LT(Boolean.class, 40),


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

    private ComparisionOp(Class<?> resultType, int priority) {
        this.resultType = resultType;
        this.priority = priority;
    }
}

package com.github.mygreen.sqlmapper.metamodel.operator;

import lombok.Getter;

/**
 * 列挙型の演算子
 *
 * @author T.TSUCHIE
 *
 */
public enum EnumOp implements Operator {

    // 列挙型
    ENUM_ORDINAL(Integer.class, -1),
    ENUM_NAME(String.class, -1),

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

    private EnumOp(Class<?> resultType, int priority) {
        this.resultType = resultType;
        this.priority = priority;
    }
}

package com.github.mygreen.sqlmapper.metamodel.operator;

/**
 * 2項演算子
 *
 *
 * @author T.TSUCHIE
 *
 */
public enum BinaryOp implements Operator {

    // General
    EQ(Boolean.class),
    NE(Boolean.class),
    IN(Boolean.class),
    NOT_IN(Boolean.class),

    // Boolean
    OR(Boolean.class),
    AND(Boolean.class),

    // String - LIKE
    CONTAINS(Boolean.class),
    STARTS(Boolean.class),
    ENDS(Boolean.class),

    // Comparable
    GOE(Boolean.class),
    GT(Boolean.class),
    LOE(Boolean.class),
    LT(Boolean.class),

    // Arithmetic
    ADD(Number.class),
    DIV(Number.class),
    MULT(Number.class),
    SUB(Number.class),
    MOD(Number.class)

    ;

    /**
     * 演算子の結果のタイプ
     */
    private final Class<?> resultType;

    private BinaryOp( Class<?> resultType) {
        this.resultType = resultType;
    }

    @Override
    public Class<?> getResultType() {
        return resultType;
    }
}

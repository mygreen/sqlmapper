package com.github.mygreen.sqlmapper.metamodel.operator;


/**
 * 単項演算子
 *
 *
 * @author T.TSUCHIE
 *
 */
public enum UnaryOp implements Operator {

    NOT(Boolean.class),
    IS_NULL(Boolean.class),
    IS_NOT_NULL(Boolean.class),

    ;

    /**
     * 演算子の結果のタイプ
     */
    private final Class<?> resultType;

    private UnaryOp( Class<?> resultType) {
        this.resultType = resultType;
    }

    @Override
    public Class<?> getResultType() {
        return resultType;
    }
}

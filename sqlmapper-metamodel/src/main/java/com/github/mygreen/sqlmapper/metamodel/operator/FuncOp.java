package com.github.mygreen.sqlmapper.metamodel.operator;


/**
 * 関数を表現する演算子
 *
 * @author T.TSUCHIE
 *
 */
public enum FuncOp implements Operator {

    // String
    LOWER(String.class),
    UPPER(String.class),

    // Date/Time
    CURRENT_DATE(Comparable.class),
    CURRENT_TIME(Comparable.class),
    CURRENT_DATE_TIME(Comparable.class),
    CURRENT_TIMESTAMP(Comparable.class),

    ;

    /**
     * 演算子の結果のタイプ
     */
    private final Class<?> resultType;

    private FuncOp( Class<?> resultType) {
        this.resultType = resultType;
    }

    @Override
    public Class<?> getResultType() {
        return resultType;
    }
}

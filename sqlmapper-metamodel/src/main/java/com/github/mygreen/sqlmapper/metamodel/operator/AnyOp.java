package com.github.mygreen.sqlmapper.metamodel.operator;


/**
 * その他の演算子
 *
 * @author T.TSUCHIE
 *
 */
public enum AnyOp implements Operator {

    BETWEEN(Boolean.class),

    // 列挙型
    ENUM_ORDINAL(Integer.class),
    ENUM_NAME(String.class),
    ;

    /**
     * 演算子の結果のタイプ
     */
    private final Class<?> resultType;

    private AnyOp( Class<?> resultType) {
        this.resultType = resultType;
    }

    @Override
    public Class<?> getResultType() {
        return resultType;
    }
}

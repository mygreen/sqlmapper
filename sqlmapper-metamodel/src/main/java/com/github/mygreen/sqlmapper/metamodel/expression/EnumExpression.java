package com.github.mygreen.sqlmapper.metamodel.expression;

/**
 * 列挙型の式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 列挙型のクラスタイプ
 */
public abstract class EnumExpression<T extends Enum<T>> extends GeneralExpression<T> {

    public EnumExpression(Expression<T> mixin) {
        super(mixin);
    }

}

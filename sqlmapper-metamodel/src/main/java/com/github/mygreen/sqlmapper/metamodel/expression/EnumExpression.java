package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.operation.NumberOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.StringOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.EnumOp;

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

    /**
     * 列挙型の序数({@link Enum#ordinal()})を式として返します
     * @return 列挙型の序数({@link Enum#ordinal()})を定数とした式
     */
    public NumberExpression<Integer> ordinal() {
        return new NumberOperation<Integer>(Integer.class, EnumOp.ENUM_ORDINAL, mixin);
    }

    /**
     * 列挙型の名称({@link Enum#name()})を式としてを返します
     * @return 列挙型の名称({@link Enum#name()})を定数とした式
     */
    public StringExpression name() {
        return new StringOperation(EnumOp.ENUM_ORDINAL, mixin);
    }

}

package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.Visitor;

import lombok.Getter;

/**
 * 定数を表現する
 *
 * @author T.TSUCHIE
 *
 */
public class Constant<T> extends ImmutableExpression<T> {

    /**
     * 定数の値
     */
    @Getter
    private final T value;

    public Constant(final Class<? extends T> type, final T value) {
        super(type);
        this.value = value;
    }

    public static Constant<String> create(String value) {
        return new Constant<String>(String.class, value);
    }

    public static Constant<Boolean> create(Boolean value) {
        return new Constant<Boolean>(Boolean.class, value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Constant<T> create(T value) {
        return new Constant<T>((Class)value.getClass(), value);
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(this, context);
    }

}

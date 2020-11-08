package com.github.mygreen.sqlmapper.metamodel.expression;

import java.util.Collection;

import com.github.mygreen.sqlmapper.metamodel.Visitor;

import lombok.Getter;

/**
 * 定数を表現する
 *
 * @parm <T> 定数のタイプ
 * @author T.TSUCHIE
 *
 */
public class Constant<T> extends ImmutableExpression<T> {

    /**
     * 定数の値
     */
    @Getter
    protected final T value;

    /**
     * 展開可能な複数の要素(インスタンスは{@link Collection})を持つ値の場合。
     */
    @Getter
    private final boolean expandable;

    public Constant(final Class<? extends T> type, final T value) {
        this(type, value, false);
    }

    public Constant(final Class<? extends T> type, final T value, boolean expandable) {
        super(type);
        this.value = value;
        this.expandable = false;
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Constant<T> create(T value, boolean expandable) {
        return new Constant<T>((Class)value.getClass(), value, expandable);
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(this, context);
    }

}

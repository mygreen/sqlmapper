package com.github.mygreen.sqlmapper.metamodel.expression;

import java.util.Collection;

import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.support.OperationUtils;

import lombok.Getter;

/**
 * 定数を表現します。
 *
 * @param <T> 定数のタイプ
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
        this.expandable = expandable;
    }

    /**
     * 文字列型の定数を作成します。
     * @param value 文字列型
     * @return 文字列型の定数
     */
    public static Constant<String> createString(String value) {
        return new Constant<String>(String.class, value);
    }

    /**
     * char型の定数を作成します。
     * @param value char型
     * @return char型の定数
     */
    public static Constant<Character> createChar(char value) {
        return new Constant<Character>(Character.class, value);
    }

    /**
     * ブーリアン型の定数を作成します。
     * @param value ブーリン案型
     * @return ブーリアン型の定数
     */
    public static Constant<Boolean> createBoolean(Boolean value) {
        return new Constant<Boolean>(Boolean.class, value);
    }

    /**
     * 汎用的な型の定数を作成ます。
     * @param <T> クラスタイプ
     * @param value 定数とする値
     * @return 汎用的な型の定数
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Constant<T> create(T value) {
        return new Constant<T>((Class)value.getClass(), value);
    }

    /**
     * {@link Collection}型/配列型などを定数として作成します。
     * @param <T> クラスタイプ
     * @param value 定数とする値
     * @return {@link Collection}型/配列型などの定数
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends Collection> Constant<T> createCollection(T value) {
        return new Constant<T>((Class)value.getClass(), value, true);
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(this, context);
    }

    /**
     * {@inheritDoc}
     * @return 式ノードを評価結果。
     */
    @Override
    public String toString() {
        return OperationUtils.toDebugString(this);
    }

}

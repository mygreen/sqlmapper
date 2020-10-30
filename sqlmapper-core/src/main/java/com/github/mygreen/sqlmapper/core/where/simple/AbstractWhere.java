package com.github.mygreen.sqlmapper.core.where.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import com.github.mygreen.sqlmapper.core.where.Where;

import lombok.NonNull;

/**
 * 演算子を保持する機能を提供をします。
 *
 * @param <T> 実装先のクラス。
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractWhere<T extends AbstractWhere<T>> implements Where {

    /**
     * 条件式のリスト
     */
    protected final List<ValueOperator> operators = new ArrayList<>();

    /**
     * {@link #eq(String, Object)}等で渡されたパラメータ値が空文字列または空白のみの文字列なら
     * <code>null</code>として扱い、 条件に加えない場合は<code>true</code>
     */
    protected boolean excludesWhitespace = false;

    /**
     * 現在の条件式を取得します。
     * @return 条件式の一覧
     */
    protected List<ValueOperator> getOperators() {
        return Collections.unmodifiableList(operators);
    }

    /**
     * SimplexWhereとして取り出します。
     * <p>取り出したときには条件式はクリアされます。</p>
     * @return
     */
    protected SimpleWhere putAsSimplexWhere() {
        SimpleWhere where = new SimpleWhere();
        where.operators.addAll(getOperators());
        this.operators.clear();

        return where;
    }

    /**
     * {@link #eq(String, Object)}等で渡されたパラメータ値が空文字列または空白のみの文字列なら
     * <code>null</code>として扱い、条件に加えないことを指定します。
     *
     * @return このインスタンス自身
     * @see #ignoreWhitespace()
     */
    @SuppressWarnings("unchecked")
    public T excludesWhitespace() {
        this.excludesWhitespace = true;
        return (T) this;
    }

    /**
     * {@link #ignoreWhitespace()}が呼び出された場合でパラメータ値が空文字列または空白のみの文字列なら
     * <code>null</code>を、 それ以外なら元の値をそのまま返します。
     *
     * @param value パラメータ値
     * @return {@link #ignoreWhitespace()}が呼び出された場合でパラメータ値が空文字列または空白のみの文字列なら
     *         <code>null</code>、 それ以外なら元の値
     */
    private Object normalize(final Object value) {
        if (excludesWhitespace && value instanceof String) {
            if (StringUtils.isEmpty(String.class.cast(value).trim())) {
                return null;
            }
        }
        return value;
    }

    private String normalizedString(final String value) {
        if (excludesWhitespace) {
            if(value == null) {
                return null;
            }

            if (StringUtils.isEmpty(value.trim())) {
                return null;
            }
        }
        return value;
    }

    private Collection<?> normalizeList(final Collection<?> values) {
        if(!excludesWhitespace || values == null) {
            return values;
        }

        final List<Object> list = new ArrayList<>(values.size());
        for(Object value : values) {
            final Object normalizedValue = normalize(value);
            if(normalizedValue != null) {
                list.add(normalizedValue);
            }
        }

        return list;
    }

    private Collection<?> normalizeArray(final Object[] values) {
        if(!excludesWhitespace || values == null) {
            return Arrays.asList(values);
        }

        final List<Object> list = new ArrayList<>(values.length);
        for(Object value : values) {
            final Object normalizedValue = normalize(value);
            if(normalizedValue != null) {
                list.add(normalizedValue);
            }
        }

        return list;
    }

    /**
     * {@literal =} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T eq(@NonNull final CharSequence propertyName, Object value) {
        value = normalize(value);
        if(SingleValueOperator.EQ.isTarget(value)) {
            this.operators.add(new SingleValueOperator.EQ(propertyName, value));
        }
        return (T) this;
    }

    /**
     * {@literal <>} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T ne(@NonNull final CharSequence propertyName, Object value) {
        value = normalize(value);
        if(SingleValueOperator.NE.isTarget(value)) {
            this.operators.add(new SingleValueOperator.NE(propertyName, value));
        }
        return (T) this;
    }

    /**
     * {@literal <} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T lt(@NonNull final CharSequence propertyName, Object value) {
        value = normalize(value);
        if(SingleValueOperator.LT.isTarget(value)) {
            this.operators.add(new SingleValueOperator.LT(propertyName, value));
        }
        return (T) this;
    }

    /**
     * {@literal <=} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T le(@NonNull final CharSequence propertyName, Object value) {
        value = normalize(value);
        if(SingleValueOperator.LE.isTarget(value)) {
            this.operators.add(new SingleValueOperator.LE(propertyName, value));
        }
        return (T) this;
    }

    /**
     * {@literal >} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T gt(@NonNull final CharSequence propertyName, Object value) {
        value = normalize(value);
        if(SingleValueOperator.GT.isTarget(value)) {
            this.operators.add(new SingleValueOperator.GT(propertyName, value));
        }
        return (T) this;
    }

    /**
     * {@literal >=} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T ge(@NonNull final CharSequence propertyName, Object value) {
        value = normalize(value);
        if(SingleValueOperator.GE.isTarget(value)) {
            this.operators.add(new SingleValueOperator.GE(propertyName, value));
        }
        return (T) this;
    }

    /**
     * {@literal LIKE} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T like(@NonNull final CharSequence propertyName, String value) {
        value = normalizedString(value);
        if(SingleValueOperator.LIKE.isTarget(value)) {
            this.operators.add(new SingleValueOperator.LIKE(propertyName, value));
        }

        return (T) this;
    }

    /**
     * {@literal LIKE} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @param escape エスケープ文字
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T like(@NonNull final CharSequence propertyName, String value, final char escape) {
        value = normalizedString(value);
        if(SingleValueOperator.LIKE_ESCAPE.isTarget(value)) {
            this.operators.add(new SingleValueOperator.LIKE_ESCAPE(propertyName, value, escape));
        }
        return (T) this;
    }

    /**
     * {@literal NOT LIKE} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T notLike(@NonNull final CharSequence propertyName, String value) {
        value = normalizedString(value);
        if(SingleValueOperator.NOT_LIKE.isTarget(value)) {
            this.operators.add(new SingleValueOperator.NOT_LIKE(propertyName, value));
        }
        return (T) this;
    }

    /**
     * {@literal NOT LIKE} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @param escape エスケープ文字
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T notLike(@NonNull final CharSequence propertyName, String value, final char escape) {
        value = normalizedString(value);
        if(SingleValueOperator.NOT_LIKE_ESCAPE.isTarget(value)) {
            this.operators.add(new SingleValueOperator.NOT_LIKE_ESCAPE(propertyName, value, escape));
        }
        return (T) this;
    }

    /**
     * {@literal LIKE '?%'} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T starts(@NonNull final CharSequence propertyName, String value) {
        value = normalizedString(value);
        if(SingleValueOperator.STARTS.isTarget(value)) {
            this.operators.add(new SingleValueOperator.STARTS(propertyName, value));
        }
        return (T) this;
    }

    /**
     * {@literal NOT LIKE '?%'} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T notStarts(@NonNull final CharSequence propertyName, String value) {
        value = normalizedString(value);
        if(SingleValueOperator.NOT_STARTS.isTarget(value)) {
            this.operators.add(new SingleValueOperator.NOT_STARTS(propertyName, value));
        }
        return (T) this;
    }

    /**
     * {@literal LIKE '%?'} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T ends(@NonNull final CharSequence propertyName, String value) {
        value = normalizedString(value);
        if(SingleValueOperator.ENDS.isTarget(value)) {
            this.operators.add(new SingleValueOperator.ENDS(propertyName, value));
        }
        return (T) this;
    }

    /**
     * {@literal NOT LIKE '%?'} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T notEnds(@NonNull final CharSequence propertyName, String value) {
        value = normalizedString(value);
        if(SingleValueOperator.NOT_ENDS.isTarget(value)) {
            this.operators.add(new SingleValueOperator.NOT_ENDS(propertyName, value));
        }
        return (T) this;
    }

    @/**
     * {@literal LIKE '%?%'} の条件を追加します。
    *
    * @param propertyName プロパティ名
    * @param value 値
    * @return この自身のインスタンス
    */SuppressWarnings("unchecked")
    public T contains(@NonNull final CharSequence propertyName, String value) {
        value = normalizedString(value);
        this.operators.add(new SingleValueOperator.CONTAINS(propertyName, value));

        return (T) this;
    }

    /**
     * {@literal NOT LIKE '%?%'} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param value 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T notContains(@NonNull final CharSequence propertyName, String value) {
        value = normalizedString(value);
        if(SingleValueOperator.NOT_CONTAINS.isTarget(value)) {
            this.operators.add(new SingleValueOperator.NOT_CONTAINS(propertyName, value));
        }

        return (T) this;
    }

    /**
     * {@literal IN} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param values 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T in(@NonNull final CharSequence propertyName, Object... values) {
        Collection<?> list = normalizeArray(values);
        if(MultiValueOperator.IN.isTarget(list)) {
            this.operators.add(new MultiValueOperator.IN(propertyName, list));
        }
        return (T) this;
    }

    /**
     * {@literal IN} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param values 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T in(@NonNull final CharSequence propertyName, Collection<?> values) {
        Collection<?> list = normalizeList(values);
        if(MultiValueOperator.IN.isTarget(list)) {
            this.operators.add(new MultiValueOperator.IN(propertyName, list));
        }
        return (T) this;
    }

    /**
     * {@literal NOT IN} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param values 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T notIn(@NonNull final CharSequence propertyName, Object... values) {
        Collection<?> list = normalizeArray(values);
        if(MultiValueOperator.NOT_IN.isTarget(list)) {
            this.operators.add(new MultiValueOperator.NOT_IN(propertyName, list));
        }

        return (T) this;
    }

    /**
     * {@literal NOT IN} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @param values 値
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T notIn(@NonNull final CharSequence propertyName, Collection<?> values) {
        Collection<?> list = normalizeList(values);
        if(MultiValueOperator.NOT_IN.isTarget(list)) {
            this.operators.add(new MultiValueOperator.NOT_IN(propertyName, list));
        }

        return (T) this;
    }

    /**
     * {@literal IS NULL} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T isNull(@NonNull final CharSequence propertyName) {
        this.operators.add(new EmptyValueOperator.IS_NULL(propertyName));

        return (T) this;
    }

    /**
     * {@literal IS NOT NULL} の条件を追加します。
     *
     * @param propertyName プロパティ名
     * @return この自身のインスタンス
     */
    @SuppressWarnings("unchecked")
    public T isNotNull(@NonNull final CharSequence propertyName) {
        this.operators.add(new EmptyValueOperator.IS_NOT_NULL(propertyName));
        return (T) this;
    }

}

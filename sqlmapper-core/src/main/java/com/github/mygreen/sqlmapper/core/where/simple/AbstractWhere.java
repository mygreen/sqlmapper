package com.github.mygreen.sqlmapper.core.where.simple;

import java.util.ArrayList;
import java.util.List;

import com.github.mygreen.sqlmapper.core.where.Where;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * 項を保持する機能を提供します。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 実装先のクラス。
 */
public abstract class AbstractWhere<T extends AbstractWhere<T>> implements Where {

    /**
     * 項のリスト。
     * 式としては{@literal AND}条件で結合されます。
     *
     */
    @Getter(AccessLevel.PROTECTED)
    protected final List<Term> terms = new ArrayList<>();

    /**
     * 式を追加します。
     * @param exp 式を指定します。プレースホルダーとして {@literal ?}が使用可能です。
     * @param values プレースホルダ―に対する値を指定します。
     * @return 自身のインスタンスを返します。
     * @throws IllegalArgumentException 指定した値の個数と式中のプレースホルダーの個数が一致しないときにスローされます。
     */
    @SuppressWarnings("unchecked")
    public T exp(@NonNull final String exp, final Object... values) {

        // 式中にプレースホルダーが存在するかチェックする。
        final int expCount = countChar(exp, '?');
        final int valueCount = values == null ? 0 : values.length;

        if(expCount != valueCount) {
            String message = String.format("exp not containing the number of placeholder '?'."
                    + "exp=[%s]."
                    + " count of in exp = %d, count of value = %d"
                    , exp, expCount, valueCount);
            throw new IllegalArgumentException(message);
        }

        addTerm(new Exp(exp, values));
        return (T)this;
    }

    /**
     * 指定した文字数をカウントします。
     * @param text 検索対象の文字列
     * @param c 検索する文字
     * @return 文字数
     */
    private static int countChar(final String text, final char c) {
        if(text == null || text.isEmpty()) {
            return 0;
        }

        int index = 0;
        int count = 0;

        int length = text.length();

        while(length > index) {
            index = text.indexOf(c, index) + 1;
            if (index == 0) {
                break;
            }
            count++;
        }

        return count;
    }

    /**
     * 項を追加します。
     * @param term 追加する項
     */
    protected void addTerm(Term term) {
        this.terms.add(term);
    }

    /**
     * 現在のインスタンスを{@link SimpleWhere} として取り出します。
     * <p>取り出したときには現在の条件式の情報はクリアされます。</p>
     * @return 現在のインスタンスを{@link SimpleWhere}
     */
    protected Where putAsSimpleWhere() {
        final SimpleWhere where = new SimpleWhere();
        where.terms.addAll(getTerms());
        this.terms.clear();

        return where;
    }
}

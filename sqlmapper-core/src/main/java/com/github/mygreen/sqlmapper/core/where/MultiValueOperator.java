package com.github.mygreen.sqlmapper.core.where;

import java.util.Collection;

import lombok.Getter;

/**
 * 値を複数指定可能な演算子の式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class MultiValueOperator implements ValueOperator {

    @Getter
    private String propertyName;

    private Object[] values;

    public MultiValueOperator(final CharSequence propertyName, final Object... values) {
        this.propertyName = propertyName.toString();
        this.values = values;
    }

    public MultiValueOperator(final CharSequence propertyName, final Collection<Object> values) {
        this.propertyName = propertyName.toString();
        this.values = values.toArray(new Object[values.size()]);
    }

    @Override
    public void accept(WhereVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * 値の個数を返します。
     * @return 値の個数
     */
    public int getValueSize() {
        return values.length;
    }

    /**
     * 指定したインデックスの値を取得します。
     * @param index 0から始まるインデックス。
     * @return 値。
     */
    public Object getValue(int index) {
        return values[index];
    }

    /**
     * 有効な値かどうか判定します。
     * <p>値がnullでない、かつ要素数が1以上のとき、有効な値と判定します。</p>
     *
     * @param values 判定対象の値。
     * @return {@literal true}のとき、有効な値と判定します。
     */
    public static boolean isTarget(final Object... values) {
        return values != null && values.length > 0;
    }

    /**
     * 有効な値かどうか判定します。
     * <p>値がnullでない、かつ要素数が1以上のとき、有効な値と判定します。</p>
     *
     * @param values 判定対象の値。
     * @return {@literal true}のとき、有効な値と判定します。
     */
    public static boolean isTarget(final Collection<Object> values) {
        return values != null && values.size() > 0;
    }

    /**
    * SQLを組み立てます。
    * @param columnName カラム名
    * @param paramNames SQLに埋め込むパラメータ名。{@link #getValueSize()} で取得できる個数分だけ渡されます。
    * @return 組み立てたSQL
    */
   public abstract String getSql(String columnName, String[] paramNames);

   public static class IN extends MultiValueOperator {

        public IN(CharSequence propertyName, Object... values) {
            super(propertyName, values);
        }

        public IN(CharSequence propertyName, Collection<Object> values) {
            super(propertyName, values);
        }

        @Override
        public String getSql(String columnName, String[] paramNames) {

            return columnName + " IN (" + String.join(", ", paramNames) + ")";
        }

   }

   public static class NOT_IN extends MultiValueOperator {

       public NOT_IN(CharSequence propertyName, Object... values) {
           super(propertyName, values);
       }

       public NOT_IN(CharSequence propertyName, Collection<Object> values) {
           super(propertyName, values);
       }

       @Override
       public String getSql(String columnName, String[] paramNames) {

           return columnName + " NOT IN (" + String.join(", ", paramNames) + ")";
       }

  }

}

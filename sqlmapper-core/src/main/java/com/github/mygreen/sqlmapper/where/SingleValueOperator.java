package com.github.mygreen.sqlmapper.where;

import lombok.Getter;

/**
 * 値を1つ指定可能な演算子の式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class SingleValueOperator<T> implements ValueOperator {

    /**
     * プロパティ名
     */
    @Getter
    private final String propertyName;

    /**
     * プロパティの値
     */
    @Getter
    private final T value;

    /**
     * コンストラクタ
     * @param propertyName プロパティ名
     * @param value プロパティの値
     */
    public SingleValueOperator(final CharSequence propertyName, final T value) {
        this.propertyName = propertyName.toString();
        this.value = value;

    }

    @Override
    public void accept(WhereVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * 有効な値かどうか判定します。
     * <p>値がnullでない、有効な値と判定します。</p>
     *
     * @param value 値が有効かどうか。
     * @return {@literal true}のとき、有効な値と判定します。
     */
    public static boolean isTarget(Object value) {
        return value != null;
    }

    /**
     * SQLを組み立てたます。
     * @param columnName カラム名
     * @param paramName SQLに埋め込むパラメータ名。
     * @return 組み立てたSQL
     */
    public abstract String getSql(String columnName, String paramName);

    public static class EQ extends SingleValueOperator<Object> {

        public EQ(CharSequence propertyName, Object value) {
            super(propertyName, value);
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " = " + paramName;
        }

    }

    public static class NE extends SingleValueOperator<Object> {

        public NE(CharSequence propertyName, Object value) {
            super(propertyName, value);
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " <> " + paramName;
        }

    }

    public static class LT extends SingleValueOperator<Object> {

        public LT(CharSequence propertyName, Object value) {
            super(propertyName, value);
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " < " + paramName;
        }

    }

    public static class LE extends SingleValueOperator<Object> {

        public LE(CharSequence propertyName, Object value) {
            super(propertyName, value);
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " <= " + paramName;
        }

    }

    public static class GT extends SingleValueOperator<Object> {

        public GT(CharSequence propertyName, Object value) {
            super(propertyName, value);
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " > " + paramName;
        }

    }

    public static class GE extends SingleValueOperator<Object> {

        public GE(CharSequence propertyName, Object value) {
            super(propertyName, value);
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " >= " + paramName;
        }

    }

    public static class LIKE extends SingleValueOperator<String> {

        public LIKE(CharSequence propertyName, String value) {
            super(propertyName, value);
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " LIKE " + paramName;
        }

    }

    public static class LIKE_ESCAPE extends SingleValueOperator<String> {

        private final char escape;

        public LIKE_ESCAPE(CharSequence propertyName, String value, char escape) {
            super(propertyName, value);
            this.escape = escape;
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " LIKE " + paramName + "ESCAPE '" + escape + "'";
        }

    }

    public static class NOT_LIKE extends SingleValueOperator<String> {

        public NOT_LIKE(CharSequence propertyName, String value) {
            super(propertyName, value);
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " NOT LIKE " + paramName;
        }

    }

    public static class NOT_LIKE_ESCAPE extends SingleValueOperator<String> {

        private final char escape;

        public NOT_LIKE_ESCAPE(CharSequence propertyName, String value, char escape) {
            super(propertyName, value);
            this.escape = escape;
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + "NOT LIKE " + paramName + "ESCAPE '" + escape + "'";
        }

    }

    public static class STARTS extends SingleValueOperator<String> {

        public STARTS(CharSequence propertyName, String value) {
            super(propertyName, value + "%");
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " LIKE " + paramName;
        }

    }

    public static class NOT_STARTS extends SingleValueOperator<String> {

        public NOT_STARTS(CharSequence propertyName, String value) {
            super(propertyName, value + "%");
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " NOT LIKE " + paramName;
        }

    }

    public static class ENDS extends SingleValueOperator<String> {

        public ENDS(CharSequence propertyName, String value) {
            super(propertyName, "%" + value);
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " LIKE " + paramName;
        }

    }

    public static class NOT_ENDS extends SingleValueOperator<String> {

        public NOT_ENDS(CharSequence propertyName, String value) {
            super(propertyName, "%" + value);
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " NOT LIKE " + paramName;
        }

    }

    public static class CONTAINS extends SingleValueOperator<String> {

        public CONTAINS(CharSequence propertyName, String value) {
            super(propertyName, "%" + value + "%");
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " LIKE " + paramName;
        }

    }

    public static class NOT_CONTAINS extends SingleValueOperator<String> {

        public NOT_CONTAINS(CharSequence propertyName, String value) {
            super(propertyName, "%" + value + "%");
        }

        @Override
        public String getSql(String columnName, String paramName) {
            return columnName + " NOT LIKE " + paramName;
        }

    }

}

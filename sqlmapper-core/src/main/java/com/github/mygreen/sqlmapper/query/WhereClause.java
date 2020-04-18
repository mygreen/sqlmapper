package com.github.mygreen.sqlmapper.query;

/**
 * where句を組み立てるクラスです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class WhereClause {

    /**
     * WHEREのキーワードです。
     */
    public static final String KEYWORD_WHERE = " WHERE ";

    /**
     * ANDのキーワードです。
     */
    public static final String KEYWORD_AND = " AND ";

    /**
     * SQLです。
     */
    private StringBuilder sql;

    /**
     * {@link WhereClause}を作成します。
     *
     */
    public WhereClause() {
        this(200);
    }

    /**
     * {@link WhereClause}を作成します。
     *
     * @param capacity 初期容量
     */
    public WhereClause(int capacity) {
        this.sql = new StringBuilder(capacity);
    }

    /**
     * SQLの長さを返します。
     *
     * @return SQLの長さ
     */
    public int getLength() {
        return sql.length();
    }

    /**
     * SQLに変換します。
     *
     * @return SQL
     */
    public String toSql() {
        return sql.toString();
    }

    /**
     * <p>
     * where句を追加します。
     * </p>
     * <p>
     * 最初に追加される条件には<code>where</code>が先頭に自動的に追加されます。
     * </p>
     * <p>
     * 2番目以降に追加される条件には<code>and</code>が先頭に自動的に追加されます。
     * </p>
     *
     * @param condition 条件
     * @return 追加したwhere句の長さを返します。
     */
    public int addAndSql(String condition) {
        int length = sql.length();
        if (length == 0) {
            sql.append(KEYWORD_WHERE).append(condition);
        } else {
            sql.append(KEYWORD_AND).append(condition);
        }
        return sql.length() - length;
    }

    /**
     * <p>
     * where句を追加します。
     * </p>
     * <p>
     * 最初に追加される条件には<code>where</code>が先頭に自動的に追加されます。
     * </p>
     *
     * @param condition 条件
     * @return 追加したwhere句の長さを返します。
     */
    public int addSql(String condition) {
        int length = sql.length();
        if (length == 0) {
            sql.append(KEYWORD_WHERE).append(condition);
        } else {
            sql.append(condition);
        }
        return sql.length() - length;
    }

    /**
     * 追加したwhere句を最後のほうから削除します。
     *
     * @param length 長さ
     */
    public void removeSql(int length) {
        sql.setLength(sql.length() - length);
    }

}

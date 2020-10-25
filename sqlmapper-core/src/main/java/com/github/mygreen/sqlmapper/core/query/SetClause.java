package com.github.mygreen.sqlmapper.core.query;

/**
 * SET句を組み立てるクラスです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SetClause {

    private final StringBuilder sql;

    public SetClause() {
        this(300);
    }

    public SetClause(int capacity) {
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
        return new String(sql);
    }

    /**
     * set句を追加します。
     *
     * @param columnName カラム名
     * @param expression 式
     */
    public void addSql(final String columnName, final String expression) {
        if (sql.length() == 0) {
            sql.append(" SET ");
        } else {
            sql.append(", ");
        }
        sql.append(columnName).append(" = ").append(expression);
    }
}

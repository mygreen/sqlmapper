package com.github.mygreen.sqlmapper.core.query;

/**
 * order by句を組み立てるクラスです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class OrderByClause {

    private final StringBuilder sql;

    public OrderByClause() {
        this.sql = new StringBuilder();
    }

    /**
     * SQLの長さを返します。
     * @return SQLの長さ
     */
    public int getLength() {
        return sql.length();
    }

    /**
     * SQLに変換します。
     * @return 組み立てたSQL
     */
    public String toSql() {
        return sql.toString();
    }

    /**
     * order by句を追加します。
     * @param orderBys order by句
     */
    public void addSql(final String... orderBys) {
        if(sql.length() == 0) {
            sql.append(" ORDER BY ");
        }
        sql.append(String.join(", ", orderBys));

    }

}

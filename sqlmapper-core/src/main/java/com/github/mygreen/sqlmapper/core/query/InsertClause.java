package com.github.mygreen.sqlmapper.query;

/**
 * INSERTのINTO句とVALUES句を組み立てるクラスです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class InsertClause {

    /**
     * INTO句を組み立てる文字列バッファ。
     */
    private final StringBuilder intoSql;

    /**
     * VALUES句を組み立てる文字列バッファ。
     */
    private final StringBuilder valuesSql;

    /**
     * インスタンスを構築します。
     */
    public InsertClause() {
        this.intoSql = new StringBuilder();
        this.valuesSql = new StringBuilder();
    }

    /**
     * インスタンスを構築します。
     * @param capacity 文字列バッファの初期容量。
     */
    public InsertClause(final int capacity) {
        this.intoSql = new StringBuilder();
        this.valuesSql = new StringBuilder();
    }

    /**
     * INTO句をSQLに変換します。
     * @return SQL
     */
    public String toIntoSql() {
        return intoSql.toString();
    }

    /**
     * VALUES句をSQLに変換します。
     * @return SQL
     */
    public String toValuesSql() {
        return valuesSql.toString();
    }

    /**
     * INTO句とVALUES句を追加します。
     * @param columnName VALUES句のカラム名
     * @param expression INSERT句の式
     */
    public void addSql(final String columnName, final String expression) {

        if(intoSql.length() == 0) {
            intoSql.append(" (").append(columnName).append(')');

            valuesSql.append(" VALUES (").append(expression).append(')');

        } else {
            intoSql.setLength(intoSql.length() - 1);
            intoSql.append(", ").append(columnName).append(')');

            valuesSql.setLength(valuesSql.length() - 1);
            valuesSql.append(", ").append(expression).append(')');

        }

    }

}

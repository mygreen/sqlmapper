package com.github.mygreen.sqlmapper.util;

/**
 * クエリ組み立て自のヘルパークラス
 *
 *
 * @author T.TSUCHIE
 *
 */
public class QueryUtils {

    /**
     * 任意の二項演算子式を組み立てる
     * @param operater 演算子
     * @param columnName カラム名（必須）
     * @param paramName バインドする変数名
     * @return 組み立てた式
     */
    public static String operand(String operater, CharSequence columnName, String paramName) {

        StringBuilder buf = new StringBuilder(10);
        buf.append(columnName)
            .append(" ").append(operater).append(" ")
            .append(paramName);
        return buf.toString();

    }

    /**
     * 演算子 {@code =} で式を組み立てます。
     * @param columnName カラム名
     * @param paramName パラメータ名
     * @return 組み立てた式
     */
    public static String EQ(CharSequence columnName, String paramName) {
        return operand("=", columnName, paramName);
    }

}

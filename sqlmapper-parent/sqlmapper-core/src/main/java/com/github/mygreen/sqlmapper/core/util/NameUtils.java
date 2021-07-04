package com.github.mygreen.sqlmapper.core.util;

import org.springframework.util.StringUtils;

/**
 * 名前に関するユーティリティクラスです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class NameUtils {

    /**
     * 先頭の文字を大文字にする。
     * <pre>
     * Utils.capitalize(null)  = null
     * Utils.capitalize("")    = ""
     * Utils.capitalize("cat") = "Cat"
     * Utils.capitalize("cAt") = "CAt"
     * </pre>
     *
     * @param str 処理対象の文字列
     * @return 引数がnull、空文字の場合、そのまま返す。
     */
    public static String capitalize(final String str) {
        final int strLen;
        if(str == null || (strLen = str.length()) == 0) {
            return str;
        }

        return new StringBuilder(strLen)
            .append(String.valueOf(str.charAt(0)).toUpperCase())
            .append(str.substring(1))
            .toString();
    }

    /**
     * 先頭の文字を小文字にする。
     *
     * @param str 変換対象の文字
     * @return 引数がnull、空文字の場合、そのまま返す。
     */
    public static String uncapitalize(final String str) {
        final int strLen;
        if(str == null || (strLen = str.length()) == 0) {
            return str;
        }

        return new StringBuilder(strLen)
            .append(String.valueOf(str.charAt(0)).toLowerCase())
            .append(str.substring(1))
            .toString();
    }

    /**
     * テーブルのカタログやスキーマを含んだ完全な名前を返します。
     *
     * @param table テーブル名
     * @param catalog カタログ名(オプション)
     * @param schema スキーマ名(オプション)
     * @return テーブルの完全な名前
     */
    public static String tableFullName(String table, String catalog, String schema) {

        StringBuilder sb = new StringBuilder();

        if(StringUtils.hasLength(catalog)) {
            sb.append(catalog).append(".");
        }

        if(StringUtils.hasLength(schema)) {
            sb.append(schema).append(".");
        }

        return sb.append(table).toString();

    }
}

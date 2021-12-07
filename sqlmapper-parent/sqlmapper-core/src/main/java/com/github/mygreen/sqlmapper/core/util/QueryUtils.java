package com.github.mygreen.sqlmapper.core.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.StringUtils;

/**
 * クエリ組み立て時のヘルパークラス
 *
 *
 * @author T.TSUCHIE
 *
 */
public class QueryUtils {

    /**
     * {@link String}の配列に変換して返します。
     * @param list 要素が文字列のリスト
     * @return {@link String}の配列
     */
    public static String[] toArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

    /**
     * 指定したインデックスのSQLパラメータソースを取得します。
     * <p>インスタンスが存在しなければ、新しく作成します。</p>
     * @param paramSources 取得対象のSQLパラメータソース。
     * @param index インデックス
     * @return SQLパラメータソース。
     */
    public static MapSqlParameterSource get(MapSqlParameterSource[] paramSources, int index) {
        MapSqlParameterSource paramSource = paramSources[index];
        if(paramSource == null) {
            paramSources[index] = new MapSqlParameterSource();
        }

        return paramSource;
    }

    /**
     * 指定したインデックスのSQLパラメータソースを取得します。
     * <p>インスタンスが存在しなければ、新しく作成します。</p>
     * @param batchParams 取得対象のSQLパラメータソース。
     * @param index インデックス
     * @return SQLパラメータソース。
     */
    public static List<Object> get(List<Object>[] batchParams, int index) {
        List<Object> params = batchParams[index];
        if(params == null) {
            batchParams[index] = new ArrayList<>();
        }
        return params;
    }

    /**
     * JdbcItemplate用のバッチ実行用のパラメータの形式に変換する。
     * @param batchParams 変換対象のパラメータ
     * @return {@literal List<Object[]>} の形式に変換したパラメータ。
     */
    public static List<Object[]> convertBatchArgs(List<Object>[] batchParams) {

        List<Object[]> batchArgs = new ArrayList<>();
        for(List<Object> params : batchParams) {
            int size = params.size();
            Object[] args = new Object[size];
            for(int i=0; i < size; i++) {
                args[i] = params.get(i);
            }
            batchArgs.add(args);
        }

        return batchArgs;

    }

    /**
     * 指定した回数文字を繰り返す。
     * @param str 繰り返す文字。(nullや空文字の場合は空文字を返す)
     * @param separator 区切り文字 (nullや空文字の場合は区切り文字は追加しない)
     * @param repeat 繰り返し数。(0以下の場合は空文字を返す)
     * @return 指定した文字を繰り返した結果。
     */
    public static String repeat(final String str, final String separator, final int repeat) {

        if(!StringUtils.hasLength(str)) {
            return "";
        }

        if(repeat <= 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for(int i=0; i < repeat; i++) {
            if(result.length() > 0 && StringUtils.hasLength(separator)) {
                result.append(separator);
            }

            result.append(str);
        }

        return result.toString();

    }

    /**
     * LIKE演算子の値に対するエスケープを行う。
     * <ul>
     *  <li>{@literal %} {@literal =>} {@literal \%}</li>
     *  <li>{@literal _} {@literal =>} {@literal \_}</li>
     * </ul>
     * @param str エスケープ対象の文字列
     * @param escape エスケープ文字
     * @return エスケープ後の文字列
     */
    public static String escapeLike(final String str, final char escape) {

        if(!StringUtils.hasLength(str)) {
            return "";
        }

        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        for(int i=0; i < length; i++) {
            char c = str.charAt(i);
            if(c == '%' || c == '_') {
                sb.append(escape).append(c);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}

package com.github.mygreen.sqlmapper.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.StringUtils;

import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.query.QueryTokenizer;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

/**
 * クエリ組み立て時のヘルパークラス
 *
 *
 * @author T.TSUCHIE
 *
 */
public class QueryUtils {

    /**
     * {@link CharSequence}の配列を{@link String}の配列に変換して返します。
     *
     * @param names {@link CharSequence}の配列
     * @return {@link String}の配列
     */
    public static String[] toStringArray(final CharSequence... names) {

        final String[] result = new String[names.length];
        for (int i = 0; i < result.length; ++i) {
            final CharSequence name = names[i];
            result[i] = name == null ? null : name.toString();
        }
        return result;

    }

    /**
     * {@link String}の配列に変換して返します。
     * @param list 要素が文字列のリスト
     * @return {@link String}の配列
     */
    public static String[] toArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

    /**
     * 関連名をベースとプロパティに分離します。
     * <p>
     * <code>aaa.bbb.ccc</code>ならベースが<code>aaa.bbb</code>、プロパティが<code>ccc</code>
     * になります。
     * </p>
     *
     * @param name 関連名
     * @return ベースとプロパティの配列
     */
    public static String[] splitBaseAndProperty(final String name) {
        String[] ret = new String[2];
        int index = name.lastIndexOf('.');
        if (index < 0) {
            ret[1] = name;
        } else {
            ret[0] = name.substring(0, index);
            ret[1] = name.substring(index + 1);
        }
        return ret;
    }

    /**
     * int型のリストを配列に変換します。
     * @param values 変換対象のリスト。
     * @return int型の配列
     */
    public static int[] toIntArray(final List<Integer> list) {

        int length = list.size();
        int[] array = new int[length];

        for(int i=0; i < length; i++) {
            array[i] = list.get(i).intValue();
        }

        return array;
    }

    /**
     * {@link PropertyMeta} のリストを配列に変換します。
     * @param list 変換対象のリスト
     * @return {@link PropertyMeta} の配列
     */
    public static PropertyMeta[] toPropertyMetaArray(final List<PropertyMeta> list) {
        int length = list.size();
        PropertyMeta[] array = new PropertyMeta[length];

        for(int i=0; i < length; i++) {
            array[i] = list.get(i);
        }

        return array;
    }

    /**
     * プロパティ名で記述されたクライテリアをカラム名に変換します。
     *
     * @param str クライテリア
     * @return カラム名で記述されたクライテリア
     */
    public static String convertCriteria(String str, EntityMeta entityMeta) {
        if (!StringUtils.hasLength(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(20 + str.length());
        QueryTokenizer tokenizer = new QueryTokenizer(str);
        for (int type = tokenizer.nextToken(); type != QueryTokenizer.TT_EOF; type = tokenizer.nextToken()) {
            String token = tokenizer.getToken();
            if (type == QueryTokenizer.TT_WORD) {
                String[] names = splitBaseAndProperty(token);
                EntityMeta targetEntityMeta = !StringUtils.hasLength(names[0]) ? entityMeta : null;
                if (targetEntityMeta == null || !targetEntityMeta.hasPropertyMeta(names[1])) {
                    sb.append(token);
                } else {
                    PropertyMeta pm = targetEntityMeta.getPropertyMeta(names[1]).get();
                    String itemName = pm.getColumnMeta().getName();
                    sb.append(itemName);
                }
            } else {
                sb.append(token);
            }
        }
        return sb.toString();
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
     * @return {@code List<Object[]>} の毛市域に変換したパラメータ。
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
            if(result.length() > 0 && !StringUtils.hasLength(separator)) {
                result.append(separator);
            }

            result.append(str);
        }

        return result.toString();

    }

    /**
     * LIKE演算子の値に対するエスケープを行う。
     * <ul>
     *  <li>{@literal %}->{@literal \%}</li>
     *  <li>{@literal _}->{@literal \_}</li>
     * </ul>
     * @param str
     * @return
     */
    public static String escapeLike(final String str) {

        if(!StringUtils.hasLength(str)) {
            return "";
        }

        return str.replaceAll("%", "\\%")
                .replaceAll("_", "\\_");
    }

    /**
     * 指定したプロパティ名がプロパティ情報に含まれているか判定します。
     * @param target 検証対象のプロパティ情報
     * @param propertyName プロパティ名
     * @return {@literal true}のとき、引数で指定した検証対象のプロパティ情報が含まれる。
     */
    public static boolean containsByPropertyName(Collection<PropertyPath<?>> target, String propertyName) {

        for(PropertyPath<?> propertyPath : target) {
            String targetPathName = propertyPath.getPathMeta().getElement();
            if(targetPathName.equals(propertyName)) {
                return true;
            }
        }

        return false;

    }

}

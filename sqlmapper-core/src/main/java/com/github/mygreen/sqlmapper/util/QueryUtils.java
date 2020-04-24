package com.github.mygreen.sqlmapper.util;

import java.util.List;

import org.springframework.util.StringUtils;

import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.query.QueryTokenizer;

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
     * 演算子 {@literal =} で式を組み立てます。
     * @param columnName カラム名
     * @param paramName パラメータ名
     * @return 組み立てた式
     */
    public static String EQ(CharSequence columnName, String paramName) {
        return operand("=", columnName, paramName);
    }

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
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(20 + str.length());
        QueryTokenizer tokenizer = new QueryTokenizer(str);
        for (int type = tokenizer.nextToken(); type != QueryTokenizer.TT_EOF; type = tokenizer.nextToken()) {
            String token = tokenizer.getToken();
            if (type == QueryTokenizer.TT_WORD) {
                String[] names = splitBaseAndProperty(token);
                EntityMeta targetEntityMeta = StringUtils.isEmpty(names[0]) ? entityMeta : null;
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

}

package com.github.mygreen.sqlmapper.localization;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * EL式中で利用可能なEL関数。
 *
 * @author T.TSUCHIE
 *
 */
public class CustomFunctions {
    
    /**
     * 文字列がnullの場合に空文字に変換する。
     * <pre class="highlight"><code class="java">
     *     CustomFunctions.defaultString(null) = ""
     *     CustomFunctions.defaultString("") = ""
     *     CustomFunctions.defaultString("abc") = "abc"
     * </code></pre>
     * 
     * @param text 判定対象の文字列
     * @return 非nullの場合は、引数の値をそのまま返す。
     */
    public static String defaultString(final String text) {
        if(text == null) {
            return "";
        }
        
        return text;
    }
    
    @SuppressWarnings("rawtypes")
    public static String join(final Object value, final String delimiter) {
        
        if(value == null) {
            return "";
        }
        
        if(value instanceof Collection) {
            return join((Collection)value, delimiter);
            
        } else if(value.getClass().isArray()) {
            Class<?> componentType = value.getClass().getComponentType();
            if(componentType.equals(Integer.TYPE)) {
                return join((int[])value, delimiter);
            } else {
                return join((Object[])value, delimiter);
            }
            
        }
        
        throw new IllegalArgumentException(String.format("arg type ('%s') is not support.", value.getClass()));
        
    }
    
    /**
     * int型の配列の値を結合する。
     * @param array 結合対象の配列
     * @param delimiter 区切り文字
     * @return 結合した文字列を返す。結合の対象の配列がnulの場合、空文字を返す。
     */
    private static String join(final int[] array, final String delimiter) {
        
        if(array == null || array.length == 0) {
            return "";
        }
        
        String value = Arrays.stream(array)
                .boxed()
                .map(String::valueOf)
                .collect(Collectors.joining(defaultString(delimiter)));
        
        return value;
    }
    
    /**
     * 配列の値を結合する。
     * @param array 結合対象の配列
     * @param delimiter 区切り文字
     * @return 結合した文字列を返す。結合の対象の配列がnulの場合、空文字を返す。
     */
    private static String join(final Object[] array, final String delimiter) {
        
        if(array == null || array.length == 0) {
            return "";
        }
        
        String value = Arrays.stream(array)
                .map(v -> v.toString())
                .collect(Collectors.joining(defaultString(delimiter)));
        
        return value;
    }
    
    /**
     * コレクションの値を結合する。
     * @param collection 結合対象のコレクション
     * @param delimiter 区切り文字
     * @return 結合した文字列を返す。結合の対象のコレクションがnulの場合、空文字を返す。
     */
    private static String join(final Collection<?> collection, final String delimiter) {
        
        if(collection == null || collection.isEmpty()) {
            return "";
        }
        
        String value = collection.stream()
                .map(v -> v.toString())
                .collect(Collectors.joining(defaultString(delimiter)));
        
        return value;
    }
    
    /**
     * 引数が空かどうか判定する。
     * <p>
     *  <li>文字列の場合は長さが0かどうか判定する。</li>
     * </p>
     * @param value
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean empty(Object value) {
        
        if(value == null) {
            return true;
        }
        
        if(value instanceof String) {
            return ((String) value).isEmpty();
            
        } else if(value instanceof Collection) {
            return ((Collection) value).isEmpty();
            
        } else if(value instanceof Map) {
            return ((Map) value).isEmpty();
            
        } else if(value.getClass().isArray()) {
            return ((Object[]) value).length == 0;
        }
        
        return value.toString().isEmpty();
        
    }
    
}

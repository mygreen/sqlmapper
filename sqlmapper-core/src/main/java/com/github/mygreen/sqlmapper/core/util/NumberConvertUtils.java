package com.github.mygreen.sqlmapper.core.util;

import org.springframework.util.StringUtils;

/**
 * 数値の変換用ユーティリティです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class NumberConvertUtils {

    /**
     * 数値を変換しかつインクリメントする。
     * 値がnullのときはnullを返す。
     * @param type 変換後のクラスタイプ
     * @param value 変換対象の値
     * @return インクリメントした値。引数valueがnullのときはnullを返す。
     * @throws IllegalArgumentException typeがサポートしていない数値型の場合。
     */
    public static Number incrementNumber(final Class<?> type, final Object value) {

        if(type == Integer.class) {
            Integer num = toInteger(value);
            if(num == null) {
                return num;
            }
            return Integer.valueOf(num + 1);

        } else if (type == int.class) {
            return toPrimitiveInteger(value) + 1;

        } else if(type == Long.class) {
            Long num = toLong(value);
            if(num == null) {
                return num;
            }
            return Long.valueOf(num + 1L);

        } else if(type == long.class) {
            return toPrimitiveLong(value) + 1L;

        } else if(type == Short.class) {
            Short num = toShort(value);
            if(num == null) {
                return num;
            }
            return Short.valueOf((short)(num + 1));

        } else if (type == short.class) {
            return toPrimitiveShort(value) + (short)1;

        }

        throw new IllegalArgumentException("not support number type : " + type.getName());

    }

    /**
     * 数値に変換する。
     * @param type 変換後のクラスタイプ
     * @param value 変換対象の値
     * @return 変換した値。引数valueがnullのときはnullを返す。
     * @throws IllegalArgumentException typeがサポートしていない数値型の場合。
     */
    public static Number convertNumber(final Class<?> type, final Object value) {

        if(type == Integer.class) {
            return toInteger(value);
        } else if (type == int.class) {
            return toPrimitiveInteger(value);
        } else if(type == Long.class) {
            return toLong(value);
        } else if(type == long.class) {
            return toPrimitiveLong(value);
        } else if(type == Short.class) {
            return toShort(value);
        } else if (type == short.class) {
            return toPrimitiveShort(value);
        }

        throw new IllegalArgumentException("not support number type : " + type.getName());

    }

    public static Integer toInteger(final Object value) {

        if(value == null) {
            return null;
        }

        if(value instanceof Number) {
            return ((Number)value).intValue();
        }

        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
        }

        if(value instanceof String) {
            return toInteger((String)value);
        }

        return toInteger(value.toString());

    }

    public static Integer toInteger(String value) {
        if(StringUtils.isEmpty(value)) {
            return null;
        }

        return Integer.valueOf(value);
    }

    public static Integer toPrimitiveInteger(final Object value) {
        if(value == null) {
            return 0;
        }

        if(value instanceof Number) {
            return ((Number)value).intValue();
        }

        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue() ? 1 : 0;
        }

        if(value instanceof String) {
            return toPrimitiveInteger((String)value);
        }

        return toPrimitiveInteger(value.toString());
    }

    public static int toPrimitiveInteger(String value) {
        if(StringUtils.isEmpty(value)) {
            return 0;
        }

        return Integer.valueOf(value).intValue();
    }

    public static Long toLong(final Object value) {

        if(value == null) {
            return null;
        }

        if(value instanceof Number) {
            return ((Number)value).longValue();
        }

        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue() ? Long.valueOf(1) : Long.valueOf(0);
        }

        if(value instanceof String) {
            return toLong((String)value);
        }

        return toLong(value.toString());

    }

    public static Long toLong(String value) {
        if(StringUtils.isEmpty(value)) {
            return null;
        }

        return Long.valueOf(value);
    }

    public static Long toPrimitiveLong(final Object value) {
        if(value == null) {
            return 0L;
        }

        if(value instanceof Number) {
            return ((Number)value).longValue();
        }

        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue() ? 1L : 0L;
        }

        if(value instanceof String) {
            return toPrimitiveLong((String)value);
        }

        return toPrimitiveLong(value.toString());
    }

    public static long toPrimitiveLong(String value) {
        if(StringUtils.isEmpty(value)) {
            return 0L;
        }

        return Long.valueOf(value).longValue();
    }

    public static Short toShort(final Object value) {

        if(value == null) {
            return null;
        }

        if(value instanceof Number) {
            return ((Number)value).shortValue();
        }

        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue() ? Short.valueOf((short)1) : Short.valueOf((short)0);
        }

        if(value instanceof String) {
            return toShort((String)value);
        }

        return toShort(value.toString());

    }

    public static Short toShort(String value) {
        if(StringUtils.isEmpty(value)) {
            return null;
        }

        return Short.valueOf(value);
    }

    public static Short toPrimitiveShort(final Object value) {
        if(value == null) {
            return 0;
        }

        if(value instanceof Number) {
            return ((Number)value).shortValue();
        }

        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue() ? (short)1 : (short)0;
        }

        if(value instanceof String) {
            return toPrimitiveShort((String)value);
        }

        return toPrimitiveShort(value.toString());
    }

    public static short toPrimitiveShort(String value) {
        if(StringUtils.isEmpty(value)) {
            return 0;
        }

        return Short.valueOf(value).shortValue();
    }

}

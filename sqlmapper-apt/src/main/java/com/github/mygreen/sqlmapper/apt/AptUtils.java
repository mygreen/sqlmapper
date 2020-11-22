package com.github.mygreen.sqlmapper.apt;

/**
 * APT処理のユーティリティクラスです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class AptUtils {

    /**
     * プリミティブ型を考慮した指定したクラス情報を取得します。
     *
     * @param className クラス名
     * @return クラス情報
     */
    public static Class<?> getClassByName(final String className) {

        if(className.equals("byte")) {
            return byte.class;

        } else if(className.equals("short")) {
            return short.class;

        } else if(className.equals("int")) {
            return int.class;

        } else if(className.equals("long")) {
            return long.class;

        } else if(className.equals("float")) {
            return float.class;

        } else if(className.equals("double")) {
            return double.class;

        } else if(className.equals("boolean")) {
            return boolean.class;

        } else if(className.equals("char")) {
            return char.class;
        }

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("not found class for name : " + className, e);
        }
    }

    /**
     * プリミティブ型の数値クラスかどうか判定します。
     * @param clazz クラス情報
     * @return {@literal true}のとき、プリミティブ型のクラス情報
     */
    public static boolean isPrimitiveNumber(Class<?> clazz) {
        if(!clazz.isPrimitive()) {
            return false;
        }

        return short.class.isAssignableFrom(clazz)
                || int.class.isAssignableFrom(clazz)
                || long.class.isAssignableFrom(clazz)
                || float.class.isAssignableFrom(clazz)
                || double.class.isAssignableFrom(clazz)
                ;

    }

    /**
     * プリミティブ型に対するラッパークラスを取得します。
     * プリミティブ型でない場合はそのまま引数を返します。
     * @param clazz クラス情報
     * @return クラス情報。
     */
    public static Class<?> getWrapperClass(Class<?> clazz) {

        if(!clazz.isPrimitive()) {
            return clazz;
        }

        if(byte.class.isAssignableFrom(clazz)) {
            return Byte.class;

        } else if(short.class.isAssignableFrom(clazz)) {
            return Short.class;

        } else if(int.class.isAssignableFrom(clazz)) {
            return Integer.class;

        } else if(long.class.isAssignableFrom(clazz)) {
            return Long.class;

        } else if(float.class.isAssignableFrom(clazz)) {
            return Float.class;

        } else if(double.class.isAssignableFrom(clazz)) {
            return Double.class;

        } else if(boolean.class.isAssignableFrom(clazz)) {
            return Boolean.class;

        } else if(char.class.isAssignableFrom(clazz)) {
            return Character.class;
        }

        return clazz;

    }

}

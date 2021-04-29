package com.github.mygreen.sqlmapper.core.util;

import java.lang.reflect.Method;

import org.springframework.util.Assert;

/**
 * クラスやメソッドに関するユーティリティクラス。
 * また、リフレクションについても処理する。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ClassUtils {

    /**
     * メソッドがアクセッサメソッド（getter/setter）か判定します。
     *
     * @param method メソッド情報
     * @return trueの場合、アクセッサメソッド。
     */
    public static boolean isAccessorMethod(final Method method) {

        return isGetterMethod(method)
                || isBooleanGetterMethod(method)
                || isSetterMethod(method);

    }

    /**
     * メソッドがgetterの書式かどうか判定する。
     * ただし、boolean型にたいするisは{@link #isBooleanGetterMethod(Method)}で判定すること。
     * <ol>
     *  <li>メソッド名が'get'か始まっていること。</li>
     *  <li>メソッド名が4文字以上であること。</li>
     *  <li>引数がないこと。</li>
     *  <li>戻り値が存在すること。</li>
     * </ol>
     * @param method メソッド情報
     * @return trueの場合はgetterメソッドである。
     */
    public static boolean isGetterMethod(final Method method) {

        final String methodName = method.getName();
        if(!methodName.startsWith("get")) {
            return false;

        } else if(methodName.length() <= 3) {
            return false;
        }

        if(method.getParameterCount() > 0) {
            return false;
        }

        if(method.getReturnType().equals(Void.class)) {
            return false;
        }

        return true;

    }

    /**
     * メソッドがsetterの書式かどうか判定する。
     * <ol>
     *  <li>メソッド名が'set'か始まっていること。</li>
     *  <li>メソッド名が4文字以上であること。</li>
     *  <li>引数が1つのみ存在すること</li>
     *  <li>戻り値は、検証しません。</li>
     * </ol>
     * @param method メソッド情報
     * @return trueの場合はsetterメソッドである。
     */
    public static boolean isSetterMethod(final Method method) {

        final String methodName = method.getName();
        if(!methodName.startsWith("set")) {
            return false;

        } else if(methodName.length() <= 3) {
            return false;
        }

        if(method.getParameterCount() != 1) {
            return false;
        }

        return true;

    }

    /**
     * メソッドがプリミティブ型のbooleanに対するgetterの書式かどうか判定する。
     * <ol>
     *  <li>メソッド名が'is'か始まっていること。</li>
     *  <li>メソッド名が3文字以上であること。</li>
     *  <li>引数がないこと。</li>
     *  <li>戻り値がプリミティブのboolean型であること。</li>
     * </ol>
     *
     * @param method メソッド情報
     * @return trueの場合はboolean型のgetterメソッドである。
     */
    public static boolean isBooleanGetterMethod(final Method method) {

        final String methodName = method.getName();
        if(!methodName.startsWith("is")) {
            return false;

        } else if(methodName.length() <= 2) {
            return false;
        }

        if(method.getParameterCount() > 0) {
            return false;
        }

        if(!isPrimitiveBoolean(method.getReturnType())) {
            return false;
        }

        return true;
    }

    /**
     * タイプがプリミティブのboolean型かどうか判定する。
     * @param type 判定対象のクラスタイプ。
     * @return trueの場合、プリミティブのboolean型。
     * @throws IllegalArgumentException {@literal type == null.}
     */
    public static boolean isPrimitiveBoolean(final Class<?> type) {
        Assert.notNull(type, "type");

        if(type.isPrimitive() && boolean.class.isAssignableFrom(type)) {
            return true;
        }

        return false;
    }

}

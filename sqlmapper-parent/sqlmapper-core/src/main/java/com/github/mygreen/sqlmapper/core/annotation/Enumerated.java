package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * プロパティが列挙型であるときのマッピング方法を指定します。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Enumerated {

    /**
     * (オプション) {@link Enum} 型のマッピング種別。
     * @return 列挙型のマッピング種別。
     */
    EnumType value();

//    String aliasMethod() default "";

    /**
     * 列挙型のマッピング種別を定義します。
     *
     *
     * @author T.TSUCHIE
     *
     */
    enum EnumType {

        /**
         * 列挙型の序数({@link Enum#ordinal()})を永続化します。
         */
        ORDINAL,
        /**
         * 列挙型の名称({@link Enum#name()})を永続化します。
         */
        STRING/*,

        ALIAS_METHOD*/;

    }

}

package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Column {

    /**
     * (オプション) カラム名を指定します。 デフォルトではプロパティもしくはフィールドの名前になります。
     *
     * @return カラム名
     */
    String name() default "";

    /**
     * (オプション) 永続化プロバイダによって生成されたSQL INSERTステートメントにカラムが含まれるかどうか。
     *
     * @return
     */
    boolean insertable() default true;

    /**
     * (オプション) 永続化プロバイダによって生成されたSQL UPDATEステートメントにカラムが含まれるかどうか。
     *
     * @return
     */
    boolean updatable() default true;

}

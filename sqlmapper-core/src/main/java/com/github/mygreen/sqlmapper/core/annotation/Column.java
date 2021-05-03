package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * カラム情報を指定します。
 * 本アノテーションが指定されていない場合は、デフォルト値が適用されます。
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Column {

    /**
     * (オプション) カラム名を指定します。
     * デフォルトではプロパティもしくはフィールドの名前になります。
     */
    String name() default "";

    /**
     * (オプション) 永続化プロバイダによって生成されたSQL INSERTステートメントにカラムが含まれるかどうか。
     */
    boolean insertable() default true;

    /**
     * (オプション) 永続化プロバイダによって生成されたSQL UPDATEステートメントにカラムが含まれるかどうか。
     */
    boolean updatable() default true;

}

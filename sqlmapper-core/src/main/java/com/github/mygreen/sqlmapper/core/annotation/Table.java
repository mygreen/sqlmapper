package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * テーブル
 *
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Table {

    /**
     * (オプション) テーブルの含まれるスキーマ。
     * デフォルトではユーザーにとっての規定のスキーマです。
     *
     * @return
     */
    String schema() default "";

    /**
     * (オプション) テーブルの含まれるカタログ。
     * デフォルトでは既定のカタログです。
     *
     * @return
     */
    String catalog() default "";


    /**
     * (オプション) テーブルの名前。
     * デフォルトの値はエンティティの名前です。
     *
     * @return
     */
    String name() default "";

}

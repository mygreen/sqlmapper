package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * テーブル情報を指定します。
 * 本アノテーションが指定されていない場合は、デフォルト値が適用されます。
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
     */
    String schema() default "";

    /**
     * (オプション) テーブルの含まれるカタログ。
     * デフォルトでは既定のカタログです。
     */
    String catalog() default "";


    /**
     * (オプション) テーブルの名前。
     * デフォルトの値はエンティティの名前です。
     */
    String name() default "";

}

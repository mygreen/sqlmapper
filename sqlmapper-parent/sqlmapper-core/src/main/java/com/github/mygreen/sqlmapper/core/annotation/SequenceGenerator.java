package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.DecimalFormat;

/**
 * 識別子(主キー)の値をシーケンスにより採番する設定をします。
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface SequenceGenerator {

    /**
     * (オプション) シーケンスの含まれるスキーマ。
     * デフォルトではユーザーにとっての規定のスキーマです。
     */
    String schema() default "";

    /**
     * (オプション) シーケンスの含まれるカタログ。
     * デフォルトでは既定のカタログです。
     */
    String catalog() default "";

    /**
     * (オプション) 主キーの値を取得するデータベースのシーケンスオブジェクトの名前。
     */
    String sequenceName() default "";

    /**
     * 識別子のクラスタイプが文字列のときに書式を設定することができます。
     * @return {@link DecimalFormat}で指定できる書式です。
     */
    String format() default "";

}

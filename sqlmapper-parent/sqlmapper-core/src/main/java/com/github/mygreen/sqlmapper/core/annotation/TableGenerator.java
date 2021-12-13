package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.DecimalFormat;

/**
 * 識別子(主キー)の値をテーブルにより採番する設定をします。
 * <p>このアノテーションは {@link Id}、{@link GeneratedValue} と併わせて使用しなければいけません。</p>
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface TableGenerator {

    /**
     * (オプション) シーケンスの含まれるスキーマ。
     * デフォルトではユーザーにとっての規定のスキーマです。
     *
     * @return
     */
    String schema() default "";

    /**
     * (オプション) シーケンスの含まれるカタログ。
     * デフォルトでは既定のカタログです。
     *
     * @return
     */
    String catalog() default "";

    /**
     * (オプション) 生成された識別子(主キー)の値を格納するテーブルの名前。
     * @return
     */
    String table() default "";

    /**
     * (オプション) テーブル内の識別子(主キー)のカラムの名前。
     * @return
     */
    String pkColumn() default "";

    /**
     * (オプション) 生成された最新の値を格納するカラムの名前。
     * @return
     */
    String valueColumn() default "";

    /**
     * (オプション) 採番する際に予め払い出しておく個数の値。
     * <p>この属性で指定した分をあらかじめ払い出しておくことで、逐次生成することによるオーバーヘッドを削減します。
     * <p>指定できる値は、1以上の整数です。</p>
     * @return
     */
    long allocationSize() default 50L;

    /**
     * (オプション) 生成された最後の値を格納するカラムを初期化するために使用される初期値。
     * <p>指定できる値は、0以上の整数です。</p>
     * @return
     */
    long initialValue() default 0L;

    /**
     * 識別子のクラスタイプが文字列のときに書式を設定することができます。
     * @return {@link DecimalFormat}で指定できる書式です。
     */
    String format() default "";

}

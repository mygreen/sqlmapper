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
     * @return スキーマ名。
     */
    String schema() default "";

    /**
     * (オプション) シーケンスの含まれるカタログ。
     * デフォルトでは既定のカタログです。
     *
     * @return カタログ名。
     */
    String catalog() default "";

    /**
     * (オプション) 生成された識別子(主キー)の値を格納するテーブルの名前。
     * @return テーブル名。
     */
    String table() default "";

    /**
     * (オプション) テーブル内の識別子(主キー)のカラムの名前。
     * @return 識別子(主キー)のカラム名。
     */
    String pkColumn() default "";

    /**
     * (オプション) 生成された最新の値を格納するカラムの名前。
     * @return 値を格納するカラム名。
     */
    String valueColumn() default "";

    /**
     * (オプション) 採番する際に予め払い出しておく個数の値。
     * <p>この属性で指定した分をあらかじめ払い出しておくことで、逐次生成することによるオーバーヘッドを削減します。
     * <p>指定できる値は、1以上の整数です。</p>
     *
     * @return 払い出しておく個数。
     */
    long allocationSize() default 50L;

    /**
     * (オプション) 生成された最後の値を格納するカラムを初期化するために使用される初期値。
     * <p>指定できる値は、0以上の整数です。</p>
     *
     * @return 初期値。
     */
    long initialValue() default 0L;

    /**
     * 識別子のクラスタイプが文字列のときに書式を設定することができます。
     * @return {@link DecimalFormat}で指定できる書式。
     */
    String format() default "";

}

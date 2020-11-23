package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
     * (オプション) 生成されたID値を格納するテーブルの名前。
     * @return
     */
    String table() default "";

    /**
     * (オプション) テーブル内の主キーのカラムの名前。
     * @return
     */
    String pkColumnName() default "";

    /**
     * (オプション) 生成された値のセットをテーブルに格納されている可能性のある他のものと区別するジェネレーターテーブルの主キーの値。
     * @return
     */
    String pkColumnValue() default "";

    /**
     *
     * @return
     */
    String valueColumnName() default "";

    /**
     * ジェネレーターが生成した値からID番号を割り当てるときにインクリメントする量。
     * <p>指定できる値は、1以上の整数です。</p>
     * @return
     */
    int allocationSize() default 50;

    /**
     * (オプション) 生成された最後の値を格納するカラムを初期化するために使用される初期値。
     * <p>指定できる値は、0以上の整数です。</p>
     * @return
     */
    int initialValue() default 0;

}

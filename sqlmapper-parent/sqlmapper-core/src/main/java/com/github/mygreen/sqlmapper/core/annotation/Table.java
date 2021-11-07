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
 * @version 0.3
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

    /**
     * (オプション) テーブルが読み取り専用かどうかを指定します。
     * <p>この属性の値が {@literal true} のときに、挿入／更新／削除 操作が呼ばれたときエラーとなります。
     *
     * @since 0.3
     */
    boolean readOnly() default false;

}

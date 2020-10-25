package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.DecimalFormat;

/**
 * 識別子(主キー)の値を自動生成する方法を定義します。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface GeneratedValue {

    /**
     * アノテーションの付いたエンティティの主キーを生成するために、
     * 永続化プロバイダが使用しなければならない主キー生成戦略。
     *
     * @return
     */
    GenerationType strategy() default GenerationType.AUTO;

    /**
     * <p>{@link GenerationType#SEQUENCE} のとき、シーケンス名が指定可能です。
     *    <br>省略した場合は、<code>テーブル名_カラム名</code> がシーケンス名となります。
     * </p>
     *
     * @return
     */
    /**
     * Springコンテナ管理のBean名を指定します。
     * @return
     */
    String generator() default "";

    /**
     * 識別子のクラスタイプが文字列のときに書式を設定することができます。
     * @return {@link DecimalFormat}で指定できる書式です。
     */
    String format() default "";

    /**
     * 主キー生成戦略の型を定義します。
     *
     * @author T.TSUCHIE
     *
     */
    enum GenerationType {
        /**
         * 永続化プロバイダが特定のデータベースに対して適切な戦略を選択しなければならないことを示します。
         */
        AUTO,
        /**
         * 永続化プロバイダはデータベースのIDカラムを使用してエンティティの主キーに値を割り当てなければならないことを示します。
         */
        IDENTITY,

        /**
         * 永続化プロバイダはデータベースのシーケンスを使用してエンティティの主キーに値を割り当てなければならないことを示します。
         */
        SEQUENCE,

        /**
         * 永続化プロバイダは一意性を保証するために基になるデータベースのテーブルを使用してエンティティの主キーに値を割り当てなければならないことを示します。
         */
        TABLE,

        /**
         * ランダムなセキュアな値
         */
        UUID;
    }
}

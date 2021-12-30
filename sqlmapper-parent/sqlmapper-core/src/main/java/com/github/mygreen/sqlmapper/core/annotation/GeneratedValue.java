package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.id.IdGenerator;

/**
 * 識別子(主キー)の値を自動生成する方法を定義します。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface GeneratedValue {

    /**
     * アノテーションの付いたエンティティの識別子(主キー)を生成するために、永続化プロバイダが使用しなければならない生成戦略。
     * <p>{@link #generator()}を指定している場合、この属性は無視されます。
     *
     * @return 識別子(主キー)の生成戦略。
     */
    GenerationType strategy() default GenerationType.AUTO;

    /**
     * 独自に識別子を生成するための、Springコンテナ管理のBean名を指定します。
     * <p>{@link IdGenerator} を実装している必要があります。
     *
     * @return 識別子の生成処理のSpring Beanの名称。
     */
    String generator() default "";

    /**
     * 主キー生成戦略の種別を定義します。
     *
     * @author T.TSUCHIE
     *
     */
    enum GenerationType {
        /**
         * 永続化プロバイダが特定のデータベースに対して適切な戦略を選択して主キーに値を割り当てます。
         * 選択される戦略は、DBダイアクレクト {@link Dialect#getDefaultGenerationType()} により決定されます。
         */
        AUTO,
        /**
         * 永続化プロバイダはデータベースのIDENTITY列を使用してエンティティの主キーに値を割り当てます。
         */
        IDENTITY,

        /**
         * 永続化プロバイダはデータベースのシーケンスを使用してエンティティの主キーに値を割り当てます。
         */
        SEQUENCE,

        /**
         * 永続化プロバイダは一意性を保証するために基になるデータベースのテーブルを使用してエンティティの主キーに値を割り当てます。
         */
        TABLE,

        /**
         * 永続化プロバイダは {@link java.util.UUID} を使用しランダムなセキュアな値を割り当てます。
         */
        UUID
        ;
    }
}

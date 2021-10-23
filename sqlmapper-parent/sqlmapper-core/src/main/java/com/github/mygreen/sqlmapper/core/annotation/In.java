package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ストアドプロシージャ／ファンクションの {@literal IN} パラメータを表すアノテーションです。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Documented
public @interface In {

    /**
     * (オプション) パラメータ名を指定します。
     * デフォルトではプロパティもしくはフィールドの名前になります。
     */
    String name() default "";
}

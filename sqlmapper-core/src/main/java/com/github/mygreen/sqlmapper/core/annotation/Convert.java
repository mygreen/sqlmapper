package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.sqlmapper.core.type.ValueType;

/**
 * プロパティの変換を指定します。
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Convert {

    /**
     * 変換処理の実装クラスを指定します。
     *
     * @return {@link ValueType}の実装クラスを指定します。
     */
    @SuppressWarnings("rawtypes")
    Class<? extends ValueType> converter();

    /**
     * SpringコンテナのBeanを参照する場合に指定します。
     * @return Springコンテナに登録しているBean名。
     */
    String name() default "";

}

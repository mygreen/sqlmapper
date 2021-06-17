package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * プロパティが時制であることを定義します。
 * <p>{@literal java.util.Date}の場合、時制の型が不明なのでこのアノテーションを付与して確定する必要があります</p>
 *
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Temporal {

    /**
     * 時制の型を指定します。
     */
    TemporalType value();

    /**
     * 時制の型を定義します。
     *
     *
     * @author T.TSUCHIE
     *
     */
    enum TemporalType {

        /**
         * {@literal java.sql.Date}と同じ意味です。
         */
        DATE,

        /**
         * {@literal java.sql.Time}と同じ意味です。
         */
        TIME,

        /**
         * {@literal java.sql.Timestamp}と同じです。
         */
        TIMESTAMP;
    }
}

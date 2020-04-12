package com.github.mygreen.sqlmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * このクラスを継承することでエンティティにマッピング情報が適用されるクラスを指定します。
 * マッピングされたスーパークラスには分割されたテーブルが定義されません。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface MappedSuperclass {
}

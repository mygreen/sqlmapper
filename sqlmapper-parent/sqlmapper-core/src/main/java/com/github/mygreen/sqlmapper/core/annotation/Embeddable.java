package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 複数のプロパティをグループ化したクラスに付与し、埋め込みクラスであることを示します。
 * <p>アノテーション {@link EmbeddedId} が付与されたプロパティのクラスに付与されている必要があります。</p>
 *
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Embeddable {
}

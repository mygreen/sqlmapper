package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * エンティティクラスの親クラスであることを定義します。
 * <p>このアノテーションを付与したクラスを継承することで、親クラスに対してもマッピング情報が適用されます。
 * <p>逆に、このアノテーションを付与していないクラスを継承しても、親クラスのプロパティに対してマッピングされませ。
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

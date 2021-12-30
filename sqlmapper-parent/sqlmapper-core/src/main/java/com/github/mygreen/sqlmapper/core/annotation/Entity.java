package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * クラスがエンティティであることを指定します。
 * このアノテーションはエンティティクラスに適用します。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Entity {

    /**
     * (オプション) エンティティの名前。SQL自動作成時のテーブルのエイリアス名などに使用されます。
     * @return エンティティの名前。
     */
    String name() default "";

}

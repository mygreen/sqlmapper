package com.github.mygreen.sqlmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.sqlmapper.audit.AuditingEntityListener;
import com.github.mygreen.sqlmapper.audit.AuditorProvider;

/**
 * レコードの修正が誰にされたかを表すプロパティに付与します。
 * <p>プロパティに値を設定するには、{@link AuditorProvider} の実装をSpringのコンテナに登録する必要があります。</p>
 * <p>{@link AuditingEntityListener} により処理されます。</p>
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface ModifiedBy {
}

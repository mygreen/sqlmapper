package com.github.mygreen.sqlmapper.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.sqlmapper.core.audit.AuditingEntityListener;

/**
 * エンティティが作成がされた日時を表すプロパティに付与します。
 * <p>{@link AuditingEntityListener} により処理されます。</p>
 *
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface CreatedAt {
}

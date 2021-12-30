package com.github.mygreen.sqlmapper.core.audit;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import com.github.mygreen.sqlmapper.core.event.PreBatchInsertEvent;
import com.github.mygreen.sqlmapper.core.event.PreBatchUpdateEvent;
import com.github.mygreen.sqlmapper.core.event.PreInsertEvent;
import com.github.mygreen.sqlmapper.core.event.PreUpdateEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyValueInvoker;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * エンティティの監査情報設定用のリスナー。
 * 監査人情報を設定する場合は、{@link AuditorProvider} の実装をSpringBeanとして登録する必要があります。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
public class AuditingEntityListener implements InitializingBean {

    /**
     * 監査人情報を提供する。
     * @param auditorProvider 監査人情報の提供処理。
     * @return 監査人情報の提供処理。
     */
    @Setter
    @Getter
    @Autowired(required=false)
    protected AuditorProvider<?> auditorProvider;

    /**
     * 監査情報を提供する {@link AuditorProvider}がSpringのコンテナに登録されていない場合は、デフォルトの空の情報を設定します。
     *
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        if(auditorProvider == null) {
            this.auditorProvider = new AuditorProvider<Object>() {

                @Override
                public Optional<Object> getCurrentAuditor() {
                    log.warn("should be injecting spring bean of " + AuditorProvider.class.getName());
                    return Optional.empty();
                }
            };
        }
    }

    /**
     * 挿入前にエンティティに監査情報を設定します。
     * @param event イベント情報。
     */
    @EventListener
    public void onPreInsert(final PreInsertEvent event) {

        final EntityMeta entityMeta = event.getEntityMeta();

        entityMeta.getCreatedAtPropertyMeta().ifPresent(p -> {
            Object value = getCurrentDateTime(p.getPropertyType());
            PropertyValueInvoker.setEmbeddedPropertyValue(p, event.getEntity(), value);
        });

        entityMeta.getUpdatedAtPropertyMeta().ifPresent(p -> {
            Object value = getCurrentDateTime(p.getPropertyType());
            PropertyValueInvoker.setEmbeddedPropertyValue(p, event.getEntity(), value);
        });

        entityMeta.getCreatedByPropertyMeta().ifPresent(p -> {
            Optional<?> value = auditorProvider.getCurrentAuditor();
            value.ifPresent(v -> PropertyValueInvoker.setEmbeddedPropertyValue(p, event.getEntity(), v));
        });

        entityMeta.getUpdatedByPropertyMeta().ifPresent(p -> {
            Optional<?> value = auditorProvider.getCurrentAuditor();
            value.ifPresent(v -> PropertyValueInvoker.setEmbeddedPropertyValue(p, event.getEntity(), v));
        });

    }

    /**
     * 更新前にエンティティに監査情報を設定します。
     * @param event イベント情報。
     */
    @EventListener
    public void onPreUpdate(final PreUpdateEvent event) {

        final EntityMeta entityMeta = event.getEntityMeta();

        entityMeta.getUpdatedAtPropertyMeta().ifPresent(p -> {
            Object value = getCurrentDateTime(p.getPropertyType());
            PropertyValueInvoker.setEmbeddedPropertyValue(p, event.getEntity(), value);
        });

        entityMeta.getUpdatedByPropertyMeta().ifPresent(p -> {
            Optional<?> value = auditorProvider.getCurrentAuditor();
            value.ifPresent(v -> PropertyValueInvoker.setEmbeddedPropertyValue(p, event.getEntity(), v));
        });

    }

    /**
     * バッチ処理による挿入前にエンティティに監査情報を設定します。
     * @param event イベント情報。
     */
    @EventListener
    public void onPreBatchInsert(final PreBatchInsertEvent event) {

        final EntityMeta entityMeta = event.getEntityMeta();

        entityMeta.getCreatedAtPropertyMeta().ifPresent(p -> {
            Object value = getCurrentDateTime(p.getPropertyType());
            for(Object entity : event.getEntities()) {
                PropertyValueInvoker.setEmbeddedPropertyValue(p, entity, value);
            }
        });

        entityMeta.getUpdatedAtPropertyMeta().ifPresent(p -> {
            Object value = getCurrentDateTime(p.getPropertyType());
            for(Object entity : event.getEntities()) {
                PropertyValueInvoker.setEmbeddedPropertyValue(p, entity, value);
            }
        });

        entityMeta.getCreatedByPropertyMeta().ifPresent(p -> {
            Optional<?> value = auditorProvider.getCurrentAuditor();
            value.ifPresent(v -> {
                for(Object entity : event.getEntities()) {
                    PropertyValueInvoker.setEmbeddedPropertyValue(p, entity, v);
                }
            });
        });

        entityMeta.getUpdatedByPropertyMeta().ifPresent(p -> {
            Optional<?> value = auditorProvider.getCurrentAuditor();
            value.ifPresent(v -> {
                for(Object entity : event.getEntities()) {
                    PropertyValueInvoker.setEmbeddedPropertyValue(p, entity, v);
                }
            });
        });

    }

    /**
     * バッチ処理による更新前にエンティティに監査情報を設定します。
     * @param event イベント情報。
     */
    @EventListener
    public void onPreBatchUpdate(final PreBatchUpdateEvent event) {

        final EntityMeta entityMeta = event.getEntityMeta();

        entityMeta.getUpdatedAtPropertyMeta().ifPresent(p -> {
            Object value = getCurrentDateTime(p.getPropertyType());
            for(Object entity : event.getEntities()) {
                PropertyValueInvoker.setEmbeddedPropertyValue(p, entity, value);
            }
        });

        entityMeta.getUpdatedByPropertyMeta().ifPresent(p -> {
            Optional<?> value = auditorProvider.getCurrentAuditor();
            value.ifPresent(v -> {
                for(Object entity : event.getEntities()) {
                    PropertyValueInvoker.setEmbeddedPropertyValue(p, entity, v);
                }
            });
        });
    }

    /**
     * 監査情報としての現在の日時を取得します。
     * @param propertyType プロパティのクラスタイプ。
     * @return プロパティのタイプに対応した現在の日時。
     * @throws IllegalArgumentException 引数で指定した {@literal propertyType} がサポートしていない日時型の場合。
     */
    protected Object getCurrentDateTime(final Class<?> propertyType) {

        if(Timestamp.class.isAssignableFrom(propertyType)) {
            return new Timestamp(System.currentTimeMillis());

        } else if(Time.class.isAssignableFrom(propertyType)) {
            return new Time(System.currentTimeMillis());

        } else if(Date.class.isAssignableFrom(propertyType)) {
            return new Date(System.currentTimeMillis());

        } else if(java.util.Date.class.isAssignableFrom(propertyType)) {
            return new java.util.Date();

        } else if(LocalDate.class.isAssignableFrom(propertyType)) {
            return LocalDate.now();

        } else if(LocalTime.class.isAssignableFrom(propertyType)) {
            return LocalTime.now();

        } else if(LocalDateTime.class.isAssignableFrom(propertyType)) {
            return LocalDateTime.now();

        }

        throw new IllegalArgumentException("not support datetime type : " + propertyType.getName());
    }

}

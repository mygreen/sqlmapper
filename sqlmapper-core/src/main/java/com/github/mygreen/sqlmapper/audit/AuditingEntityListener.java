package com.github.mygreen.sqlmapper.audit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import com.github.mygreen.sqlmapper.event.PreBatchInsertEvent;
import com.github.mygreen.sqlmapper.event.PreInsertEvent;
import com.github.mygreen.sqlmapper.event.PreUpdateEvent;
import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.meta.PropertyValueInvoker;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * エンティティの監査情報設定用のリスナー。
 * 監査人情報を設定する場合は、{@link AuditorProvider} の実装をBeanとして登録する必要があります。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
public class AuditingEntityListener implements InitializingBean {

    /**
     * 監査人情報を提供する。
     */
    @Setter
    @Getter
    @Autowired(required=false)
    protected AuditorProvider<?> auditorProvider;

    /**
     * {@inheritDoc}
     * 監査情報を提供する {@link AuditorProvider}がSpringのコンテナに登録されていない場合は、
     * デフォルトの空の情報を設定します。
     */
    @Override
    public void afterPropertiesSet() throws Exception {
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
            PropertyValueInvoker.setPropertyValue(p, event.getEntity(), value);
        });

        entityMeta.getModifiedAtPropertyMeta().ifPresent(p -> {
            Object value = getCurrentDateTime(p.getPropertyType());
            PropertyValueInvoker.setPropertyValue(p, event.getEntity(), value);
        });

        entityMeta.getCreatedByPropertyMeta().ifPresent(p -> {
            Optional<?> value = auditorProvider.getCurrentAuditor();
            value.ifPresent(v -> PropertyValueInvoker.setPropertyValue(p, event.getEntity(), v));
        });

        entityMeta.getModifiedByPropertyMeta().ifPresent(p -> {
            Optional<?> value = auditorProvider.getCurrentAuditor();
            value.ifPresent(v -> PropertyValueInvoker.setPropertyValue(p, event.getEntity(), v));
        });

    }

    /**
     * 更新前にエンティティに監査情報を設定します。
     * @param event イベント情報。
     */
    @EventListener
    public void onPreUpdate(final PreUpdateEvent event) {

        final EntityMeta entityMeta = event.getEntityMeta();

        entityMeta.getModifiedAtPropertyMeta().ifPresent(p -> {
            Object value = getCurrentDateTime(p.getPropertyType());
            PropertyValueInvoker.setPropertyValue(p, event.getEntity(), value);
        });

        entityMeta.getModifiedByPropertyMeta().ifPresent(p -> {
            Optional<?> value = auditorProvider.getCurrentAuditor();
            value.ifPresent(v -> PropertyValueInvoker.setPropertyValue(p, event.getEntity(), v));
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
                PropertyValueInvoker.setPropertyValue(p, entity, value);
            }
        });

        entityMeta.getModifiedAtPropertyMeta().ifPresent(p -> {
            Object value = getCurrentDateTime(p.getPropertyType());
            for(Object entity : event.getEntities()) {
                PropertyValueInvoker.setPropertyValue(p, entity, value);
            }
        });

        entityMeta.getCreatedByPropertyMeta().ifPresent(p -> {
            Optional<?> value = auditorProvider.getCurrentAuditor();
            value.ifPresent(v -> {
                for(Object entity : event.getEntities()) {
                    PropertyValueInvoker.setPropertyValue(p, entity, v);
                }
            });
        });

        entityMeta.getModifiedByPropertyMeta().ifPresent(p -> {
            Optional<?> value = auditorProvider.getCurrentAuditor();
            value.ifPresent(v -> {
                for(Object entity : event.getEntities()) {
                    PropertyValueInvoker.setPropertyValue(p, entity, v);
                }
            });
        });

    }

    /**
     * バッチ処理による更新前にエンティティに監査情報を設定します。
     * @param event イベント情報。
     */
    @EventListener
    public void onPreBatchUpdate(final PreBatchInsertEvent event) {

        final EntityMeta entityMeta = event.getEntityMeta();

        entityMeta.getModifiedAtPropertyMeta().ifPresent(p -> {
            Object value = getCurrentDateTime(p.getPropertyType());
            for(Object entity : event.getEntities()) {
                PropertyValueInvoker.setPropertyValue(p, entity, value);
            }
        });

        entityMeta.getModifiedByPropertyMeta().ifPresent(p -> {
            Optional<?> value = auditorProvider.getCurrentAuditor();
            value.ifPresent(v -> {
                for(Object entity : event.getEntities()) {
                    PropertyValueInvoker.setPropertyValue(p, entity, v);
                }
            });
        });
    }

    /**
     * 監査情報としての現在の日時を取得します。
     * @param propertyType プロパティのクラスタイプ。
     * @return プロパティのタイプに対応した現在の日時。
     * @throws IllegalArgumentException typeがサポートしていない日時型の場合。
     */
    protected Object getCurrentDateTime(final Class<?> propertyType) {
        if(Date.class.isAssignableFrom(propertyType)) {
            // java.util.Date の子クラス(java.sql.XXXX) の場合
            return new Date();

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

package com.github.mygreen.sqlmapper.event.listener;

import java.util.Optional;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import com.github.mygreen.sqlmapper.event.PreBatchInsertEvent;
import com.github.mygreen.sqlmapper.event.PreInsertEvent;
import com.github.mygreen.sqlmapper.event.PreUpdateEvent;
import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.util.QueryUtils;

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

    @Override
    public void afterPropertiesSet() throws Exception {
        // auditorProviderが登録されていない場合は、デフォルトの空の情報を設定します。
        if(auditorProvider == null) {
            this.auditorProvider = new AuditorProvider<Object>() {

                @Override
                public Optional<Object> getCurrentAuditor() {
                    log.warn("should injection bean " + AuditorProvider.class.getName());
                    return Optional.empty();
                }
            };
        }
    }

    @EventListener
    public void onPreInsertEvent(final PreInsertEvent event) {

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

    @EventListener
    public void onPreUpdateEvent(final PreUpdateEvent event) {

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

    @EventListener
    public void onPreBatchInsertEvent(final PreBatchInsertEvent event) {

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

    @EventListener
    public void onPreBatchUpdateEvent(final PreBatchInsertEvent event) {

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
     */
    protected Object getCurrentDateTime(final Class<?> propertyType) {
        return QueryUtils.now(propertyType);
    }

}

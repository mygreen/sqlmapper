package com.github.mygreen.sqlmapper.event;

import com.github.mygreen.sqlmapper.meta.EntityMeta;

public class PreBatchDeleteEvent extends AbstractEntityEvent {

    public PreBatchDeleteEvent(Object source, EntityMeta entityMeta, Object entity) {
        super(source, entityMeta, entity);
    }
}

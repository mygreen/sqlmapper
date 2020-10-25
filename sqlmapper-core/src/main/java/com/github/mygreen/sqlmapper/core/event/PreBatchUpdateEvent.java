package com.github.mygreen.sqlmapper.core.event;

import com.github.mygreen.sqlmapper.core.meta.EntityMeta;

public class PreBatchUpdateEvent extends AbstractEntityBatchEvent {

    public PreBatchUpdateEvent(Object source, EntityMeta entityMeta, Object[] entities) {
        super(source, entityMeta, entities);
    }

}

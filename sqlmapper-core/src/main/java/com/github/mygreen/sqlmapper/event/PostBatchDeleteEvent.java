package com.github.mygreen.sqlmapper.event;

import com.github.mygreen.sqlmapper.meta.EntityMeta;

public class PostBatchDeleteEvent extends AbstractEntityBatchEvent {

    public PostBatchDeleteEvent(Object source, EntityMeta entityMeta, Object[] entities) {
        super(source, entityMeta, entities);
    }
}

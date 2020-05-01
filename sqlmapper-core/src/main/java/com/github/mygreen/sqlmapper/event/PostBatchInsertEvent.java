package com.github.mygreen.sqlmapper.event;

import com.github.mygreen.sqlmapper.meta.EntityMeta;

public class PostBatchInsertEvent extends AbstractEntityBatchEvent {

    public PostBatchInsertEvent(Object source, EntityMeta entityMeta, Object[] entities) {
        super(source, entityMeta, entities);
    }
}

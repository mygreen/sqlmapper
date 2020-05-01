package com.github.mygreen.sqlmapper.event;

import com.github.mygreen.sqlmapper.meta.EntityMeta;

public class PostUpdateEvent extends AbstractEntityEvent {

    public PostUpdateEvent(Object source, EntityMeta entityMeta, Object entity) {
        super(source, entityMeta, entity);
    }
}

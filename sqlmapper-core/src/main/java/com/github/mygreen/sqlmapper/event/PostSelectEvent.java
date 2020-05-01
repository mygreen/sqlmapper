package com.github.mygreen.sqlmapper.event;

import com.github.mygreen.sqlmapper.meta.EntityMeta;

public class PostSelectEvent extends AbstractEntityEvent {

    public PostSelectEvent(Object source, EntityMeta entityMeta, Object entity) {
        super(source, entityMeta, entity);
    }
}

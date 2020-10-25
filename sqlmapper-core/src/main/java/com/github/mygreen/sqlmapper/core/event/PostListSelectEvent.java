package com.github.mygreen.sqlmapper.core.event;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.github.mygreen.sqlmapper.core.meta.EntityMeta;

import lombok.Getter;

public class PostListSelectEvent extends ApplicationEvent {

    /**
     * 処理対象のエンティティのメタ情報
     */
    @Getter
    protected final EntityMeta entityMeta;

    /**
     * 処理対象のエンティティのインスタンス
     */
    @Getter
    protected final List<?> entities;

    public PostListSelectEvent(Object source, EntityMeta entityMeta, List<?> entities) {
        super(source);
        this.entityMeta = entityMeta;
        this.entities = entities;
    }

}

package com.github.mygreen.sqlmapper.event;

import org.springframework.context.ApplicationEvent;

import com.github.mygreen.sqlmapper.meta.EntityMeta;

import lombok.Getter;

/**
 * エンティティの処理に対するイベントの親クラス。
 *
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractEntityEvent extends ApplicationEvent {

    /**
     * 処理対象のエンティティのメタ情報
     */
    @Getter
    protected final EntityMeta entityMeta;

    /**
     * 処理対象のエンティティのインスタンス
     */
    @Getter
    protected final Object entity;

    public AbstractEntityEvent(Object source, EntityMeta entityMeta, Object entity) {
        super(source);
        this.entityMeta = entityMeta;
        this.entity = entity;
    }

}

package com.github.mygreen.sqlmapper.core.event;

import org.springframework.context.ApplicationEvent;

import com.github.mygreen.sqlmapper.core.meta.EntityMeta;

import lombok.Getter;

/**
 * エンティティのバッチ処理に対するイベントの親クラス。
 *
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractEntityBatchEvent extends ApplicationEvent {

    /**
     * 処理対象のエンティティのメタ情報
     */
    @Getter
    protected final EntityMeta entityMeta;

    /**
     * 処理対象のエンティティのインスタンス
     */
    @Getter
    protected final Object[] entities;

    public AbstractEntityBatchEvent(Object source, EntityMeta entityMeta, Object[] entities) {
        super(source);
        this.entityMeta = entityMeta;
        this.entities = entities;
    }

}

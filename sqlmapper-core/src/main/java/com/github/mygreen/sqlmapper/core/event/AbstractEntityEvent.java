package com.github.mygreen.sqlmapper.core.event;

import org.springframework.context.ApplicationEvent;

import com.github.mygreen.sqlmapper.core.meta.EntityMeta;

import lombok.Getter;

/**
 * エンティティの処理に対するイベントの親クラスです。
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

    /**
     * コンストラクタです。
     *
     * @param source イベント発生個所のクラスのインスタンス
     * @param entityMeta 処理対象のエンティティのメタ情報です。
     * @param entity 処理対象のエンティティのインスタンスです。
     */
    public AbstractEntityEvent(Object source, EntityMeta entityMeta, Object entity) {
        super(source);
        this.entityMeta = entityMeta;
        this.entity = entity;
    }

}

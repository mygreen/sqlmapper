package com.github.mygreen.sqlmapper.core.event;

import com.github.mygreen.sqlmapper.core.meta.EntityMeta;

/**
 * エンティティに対する挿入後のイベントです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class PostInsertEvent extends AbstractEntityEvent {

    /**
     * コンストラクタです。
     *
     * @param source イベント発生個所のクラスのインスタンス
     * @param entityMeta 処理対象のエンティティのメタ情報です。
     * @param entity 処理対象のエンティティのインスタンスです。
     */
    public PostInsertEvent(Object source, EntityMeta entityMeta, Object entity) {
        super(source, entityMeta, entity);
    }
}

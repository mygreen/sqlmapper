package com.github.mygreen.sqlmapper.core.event;

import com.github.mygreen.sqlmapper.core.meta.EntityMeta;

/**
 * エンティティを参照後のイベントです。
 * <p>参照したエンティティがnullの場合は、実行されません。</p>
 *
 * @author T.TSUCHIE
 *
 */
public class PostSelectEvent extends AbstractEntityEvent {

    /**
     * コンストラクタです。
     *
     * @param source イベント発生個所のクラスのインスタンス
     * @param entityMeta 処理対象のエンティティのメタ情報です。
     * @param entity 処理対象のエンティティのインスタンスです。
     */
    public PostSelectEvent(Object source, EntityMeta entityMeta, Object entity) {
        super(source, entityMeta, entity);
    }
}

package com.github.mygreen.sqlmapper.core.event;

import com.github.mygreen.sqlmapper.core.meta.EntityMeta;

/**
 * エンティティに対するバッチ挿入前のイベントです。
 *
 * @author T.TSUCHIE
 *
 */
public class PreBatchInsertEvent extends AbstractEntityBatchEvent {

    /**
     * コンストラクタです。
     *
     * @param source イベント発生個所のクラスのインスタンス
     * @param entityMeta 処理対象のエンティティのメタ情報です。
     * @param entities 処理対象のエンティティのインスタンスです。
     */
    public PreBatchInsertEvent(Object source, EntityMeta entityMeta, Object[] entities) {
        super(source, entityMeta, entities);
    }
}

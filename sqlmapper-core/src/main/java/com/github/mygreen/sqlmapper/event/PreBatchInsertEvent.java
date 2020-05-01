package com.github.mygreen.sqlmapper.event;

import com.github.mygreen.sqlmapper.meta.EntityMeta;

/**
 * エンティティのバッチ挿入実行前のイベント
 *
 * @author T.TSUCHIE
 *
 */
public class PreBatchInsertEvent extends AbstractEntityBatchEvent {

    public PreBatchInsertEvent(Object source, EntityMeta entityMeta, Object[] entities) {
        super(source, entityMeta, entities);
    }
}

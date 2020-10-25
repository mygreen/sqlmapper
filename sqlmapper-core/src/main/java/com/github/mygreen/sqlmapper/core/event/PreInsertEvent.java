package com.github.mygreen.sqlmapper.core.event;

import com.github.mygreen.sqlmapper.core.meta.EntityMeta;

/**
 * エンティティの挿入実行前のイベント
 *
 * @author T.TSUCHIE
 *
 */
public class PreInsertEvent extends AbstractEntityEvent {

    public PreInsertEvent(Object source, EntityMeta entityMeta, Object entity) {
        super(source, entityMeta, entity);
    }

}

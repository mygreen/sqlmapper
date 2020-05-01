package com.github.mygreen.sqlmapper.event;

import com.github.mygreen.sqlmapper.meta.EntityMeta;

/**
 * エンティティを参照処理のイベントです。
 * <p>参照したエンティティがnullの場合は、実行されません。</p>
 *
 * @author T.TSUCHIE
 *
 */
public class PostSelectEvent extends AbstractEntityEvent {

    public PostSelectEvent(Object source, EntityMeta entityMeta, Object entity) {
        super(source, entityMeta, entity);
    }
}

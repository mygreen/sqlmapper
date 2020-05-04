package com.github.mygreen.sqlmapper.query;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.meta.EntityMeta;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * エンティティに対するクエリを実行するための抽象クラスです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractEntityQuery<T> extends QueryBase<T> {

    @Getter(AccessLevel.PROTECTED)
    protected final EntityMeta entityMeta;

    public AbstractEntityQuery(SqlMapperContext context, EntityMeta entityMeta) {
        super(context);
        this.entityMeta = entityMeta;
    }
}

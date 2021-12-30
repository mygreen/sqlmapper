package com.github.mygreen.sqlmapper.core.test.entity;

import java.io.Serializable;
import java.util.Date;

import com.github.mygreen.sqlmapper.core.annotation.CreatedAt;
import com.github.mygreen.sqlmapper.core.annotation.MappedSuperclass;
import com.github.mygreen.sqlmapper.core.annotation.Temporal;
import com.github.mygreen.sqlmapper.core.annotation.Temporal.TemporalType;
import com.github.mygreen.sqlmapper.core.annotation.UpdatedAt;
import com.github.mygreen.sqlmapper.core.annotation.Version;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * エンティティのベースクラス。
 * バージョンキーなどの共通のカラムを定義する。
 *
 */
@EqualsAndHashCode
@Getter
@Setter
@MappedSuperclass
public abstract class EntityBase implements Serializable {

    @CreatedAt
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createAt;

    @UpdatedAt
    @Temporal(TemporalType.TIMESTAMP)
    protected Date updateAt;

    @Version
    protected Long version;
}

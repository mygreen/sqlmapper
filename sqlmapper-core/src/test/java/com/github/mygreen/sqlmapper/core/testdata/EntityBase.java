package com.github.mygreen.sqlmapper.core.testdata;

import java.util.Date;

import com.github.mygreen.sqlmapper.core.annotation.CreatedAt;
import com.github.mygreen.sqlmapper.core.annotation.MappedSuperclass;
import com.github.mygreen.sqlmapper.core.annotation.ModifiedAt;
import com.github.mygreen.sqlmapper.core.annotation.Temporal;
import com.github.mygreen.sqlmapper.core.annotation.Temporal.TemporalType;
import com.github.mygreen.sqlmapper.core.annotation.Version;

import lombok.Getter;
import lombok.Setter;

/**
 * エンティティのベースクラス。
 * バージョンキーなどの共通のカラムを定義する。
 *
 *
 */
@MappedSuperclass
public abstract class EntityBase {

    @Getter
    @Setter
    @CreatedAt
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @Getter
    @Setter
    @ModifiedAt
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;

    @Getter
    @Setter
    @Version
    private long version;
}

package com.github.mygreen.sqlmapper.apt.domain;

import java.io.Serializable;
import java.util.Date;

import com.github.mygreen.sqlmapper.core.annotation.CreatedAt;
import com.github.mygreen.sqlmapper.core.annotation.MappedSuperclass;
import com.github.mygreen.sqlmapper.core.annotation.UpdatedAt;
import com.github.mygreen.sqlmapper.core.annotation.Temporal;
import com.github.mygreen.sqlmapper.core.annotation.Temporal.TemporalType;
import com.github.mygreen.sqlmapper.core.annotation.Version;

import lombok.Getter;
import lombok.Setter;

/**
 * エンティティのベースとなる親クラス
 *
 * @author T.TSUCHIE
 *
 */
@MappedSuperclass
public abstract class EntityBase implements Serializable {

    @Getter
    @Setter
    @CreatedAt
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createAt;

    @Getter
    @Setter
    @UpdatedAt
    @Temporal(TemporalType.TIMESTAMP)
    protected Date updateAt;

    @Getter
    @Setter
    @Version
    protected long version;

}

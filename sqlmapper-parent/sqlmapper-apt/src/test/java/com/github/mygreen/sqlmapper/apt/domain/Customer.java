package com.github.mygreen.sqlmapper.apt.domain;

import java.sql.Timestamp;

import com.github.mygreen.sqlmapper.core.annotation.Column;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.UpdatedAt;
import com.github.mygreen.sqlmapper.core.annotation.Version;

import lombok.Data;

/**
 * テスト用のドメインクラス
 *
 * @author T.TSUCHIE
 *
 */
@Data
@Entity
public class Customer {

    @Id
    private Long id;

    private String name;

    private Integer age;

    @Column
    private Role role;

    @Column
    private boolean deleted;

    @UpdatedAt
    private Timestamp updateAt;

    @Version
    private long version;

}

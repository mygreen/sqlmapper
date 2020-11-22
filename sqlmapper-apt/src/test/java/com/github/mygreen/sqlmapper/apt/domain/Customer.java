package com.github.mygreen.sqlmapper.apt.domain;

import java.sql.Timestamp;

import com.github.mygreen.sqlmapper.core.annotation.Column;
import com.github.mygreen.sqlmapper.core.annotation.Entity;

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

    @Column
    private String name;

    @Column
    private Integer age;

    @Column
    private Role role;

    @Column
    private boolean deleted;

    @Column
    private Timestamp updateAt;

}

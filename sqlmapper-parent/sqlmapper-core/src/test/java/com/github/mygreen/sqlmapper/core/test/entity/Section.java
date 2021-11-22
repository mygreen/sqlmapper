package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Transient;

import lombok.Data;

/**
 * 部門
 *
 * @author T.TSUCHIE
 *
 */
@Entity
@Data
public class Section {

    /**
     * 部門コード
     */
    @Id
    private String code;

    /**
     * 事業所コード
     */
    @Id
    private int businessEstablishmentCode;

    /**
     * 部門名
     */
    private String name;

    @Transient
    private BusinessEstablishment businessEstablishment;

}

package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;

import lombok.Data;

/**
 * 事業所
 *
 * @author T.TSUCHIE
 *
 */
@Entity
@Data
public class BusinessEstablishment {

    /**
     * 事業所コード
     */
    @Id
    private int code;

    /**
     * 事業所名
     */
    private String name;

    /**
     * 住所
     */
    private String address;

    /**
     * 電話番号
     */
    private String tel;

}

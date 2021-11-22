package com.github.mygreen.sqlmapper.core.test.entity;

import java.time.LocalDate;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Enumerated;
import com.github.mygreen.sqlmapper.core.annotation.Enumerated.EnumType;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Transient;
import com.github.mygreen.sqlmapper.core.annotation.Version;

import lombok.Data;

@Entity
@Data
public class Employee {

	/**
	 * 社員ID
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 氏名
     */
    private String name;

    /**
     * 年齢
     */
    private int age;

    /**
     * 役職
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * 入社日
     */
    private LocalDate hireDate;

    /**
     * 部門コード
     */
    private String sectionCode;

    /**
     * 事業所コード
     */
    private Integer businessEstablishmentCode;

    /**
     * バージョンキー
     */
    @Version
    private long version;

    /**
     * 部門情報
     */
    @Transient
    private Section section;

}

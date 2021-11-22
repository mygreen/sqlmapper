package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;

import lombok.Data;

/**
 * 自動生成の主キーテーブルで作成する。
 *
 *
 */
@Data
@Entity
public class SampleTableId1 {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    public Long id;

    public String value;

}

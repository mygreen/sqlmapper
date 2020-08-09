package com.github.mygreen.sqlmapper.testdata;

import com.github.mygreen.sqlmapper.annotation.Entity;
import com.github.mygreen.sqlmapper.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.annotation.Id;

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

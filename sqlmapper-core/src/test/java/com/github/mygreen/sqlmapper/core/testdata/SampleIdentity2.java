package com.github.mygreen.sqlmapper.core.testdata;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;

import lombok.Data;

/**
 * 自動生成の主キーが２つ
 *
 *
 */
@Data
@Entity
public class SampleIdentity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, format="00000")
    public String id2;

    public String value;

}

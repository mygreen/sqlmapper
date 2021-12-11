package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.Data;

/**
 * 自動生成の主キーが２つ
 *
 *
 */
@Data
@Table(name = "test_generated_id_identity2")
@Entity
public class TestGeneratedIdIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, format="00000")
    public String id2;

    public String value;

}

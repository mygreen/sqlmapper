package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.Data;

/**
 * {@literal GeneratedValue(strategy=IDENTITY)}のテスト
 *
 *
 * @author T.TSUCHIE
 *
 */
@Data
@Table(name = "test_generated_value_identity")
@Entity
public class GeneratedValueIdentityTestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;
}

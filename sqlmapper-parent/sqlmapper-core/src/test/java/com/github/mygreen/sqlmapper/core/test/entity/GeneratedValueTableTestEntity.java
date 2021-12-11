package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.Data;

/**
 * {@literal GeneratedValue(strategy=TABLE)}のテスト
 *
 *
 * @author T.TSUCHIE
 *
 */
@Data
@Table(name = "test_generated_value_table")
@Entity
public class GeneratedValueTableTestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private String comment;
}

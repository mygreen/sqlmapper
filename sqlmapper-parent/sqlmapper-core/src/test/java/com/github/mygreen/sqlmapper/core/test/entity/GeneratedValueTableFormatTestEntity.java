package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;
import com.github.mygreen.sqlmapper.core.annotation.TableGenerator;

import lombok.Data;

/**
 * {@literal GeneratedValue(strategy=TABLE)}のテスト
 *
 *
 * @author T.TSUCHIE
 *
 */
@Data
@Table(name = "test_generated_value_table_format")
@Entity
public class GeneratedValueTableFormatTestEntity {

    @Id
    @TableGenerator(format = "00000000")
    @GeneratedValue(strategy = GenerationType.TABLE)
    private String id;

    private String comment;
}

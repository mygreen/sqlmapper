package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.SequenceGenerator;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.Data;

/**
 * {@literal GeneratedValue(strategy=SEQUENCE)}のテスト
 *
 *
 * @author T.TSUCHIE
 *
 */
@Data
@Table(name = "test_generated_value_sequence")
@Entity
public class GeneratedValueSequenceTestEntity {

    @Id
    @SequenceGenerator(sequenceName = "test_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String comment;
}

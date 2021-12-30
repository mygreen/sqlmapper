package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.SequenceGenerator;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.Data;

/**
 * {@literal GeneratedValue(strategy=SEQUENCE)}のテスト。
 * <p>文字列型にフォーマット指定。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Data
@Table(name = "test_generated_value_sequence_format")
@Entity
public class GeneratedValueSequenceFormatTestEntity {

    @Id
    @SequenceGenerator(sequenceName = "test_sequence", format = "00000000")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;

    private String comment;
}

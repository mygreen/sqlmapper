package com.github.mygreen.sqlmapper.core.test.entity;

import java.util.UUID;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.Data;

/**
 * {@literal GeneratedValue(strategy=UUID)}のテスト
 *
 *
 * @author T.TSUCHIE
 *
 */
@Data
@Table(name = "test_generated_value_uuid")
@Entity
public class GeneratedValueUUIDTestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String comment;
}

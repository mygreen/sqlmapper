package com.github.mygreen.sqlmapper.core.test.entity;

import java.io.Serializable;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.Data;

@Data
@Table(name = "test_type_value_integer_number")
@Entity
public class TypeValueIntegerNumberTestEntity implements Serializable {

    @Id
    private long id;

    private Short shortData;

    private Integer intData;

    private Long longData;

    private String comment;
}

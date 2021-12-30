package com.github.mygreen.sqlmapper.core.test.entity;

import java.io.Serializable;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.Data;

@Data
@Table(name = "test_type_value_primitive_number")
@Entity
public class TypeValuePrimitiveNumberTestEntity implements Serializable {

    @Id
    private long id;

    private short shortData;

    private int intData;

    private long longData;

    private float floatData;

    private double doubleData;

    private String comment;
}

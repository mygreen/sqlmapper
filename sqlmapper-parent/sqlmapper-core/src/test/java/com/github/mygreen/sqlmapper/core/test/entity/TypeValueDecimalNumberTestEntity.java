package com.github.mygreen.sqlmapper.core.test.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.Data;

@Data
@Table(name = "test_type_value_decimal_number")
@Entity
public class TypeValueDecimalNumberTestEntity implements Serializable {

    @Id
    private long id;

    private Float floatData;

    private Double doubleData;

    private BigDecimal bigdecimalData;

    private String comment;
}

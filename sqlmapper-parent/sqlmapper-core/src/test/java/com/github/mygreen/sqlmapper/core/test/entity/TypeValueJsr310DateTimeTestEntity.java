package com.github.mygreen.sqlmapper.core.test.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.Data;

@Data
@Table(name = "test_type_value_jsr310_date_time")
@Entity
public class TypeValueJsr310DateTimeTestEntity implements Serializable {

    @Id
    private long id;

    private LocalDate dateData;

    private LocalTime timeData;

    private LocalDateTime timestampData;

    private String comment;


}

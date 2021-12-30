package com.github.mygreen.sqlmapper.core.test.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.Data;

@Data
@Table(name = "test_type_value_sql_date_time")
@Entity
public class TypeValueSqlDateTimeTestEntity implements Serializable {

    @Id
    private long id;

    private Date dateData;

    private Time timeData;

    private Timestamp timestampData;

    private String comment;


}

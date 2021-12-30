package com.github.mygreen.sqlmapper.core.test.entity;

import java.io.Serializable;
import java.util.Date;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;
import com.github.mygreen.sqlmapper.core.annotation.Temporal;
import com.github.mygreen.sqlmapper.core.annotation.Temporal.TemporalType;

import lombok.Data;

@Data
@Table(name = "test_type_value_util_date_time")
@Entity
public class TypeValueUtilDateTimeTestEntity implements Serializable {

    @Id
    private long id;

    @Temporal(TemporalType.DATE)
    private Date dateData;

    @Temporal(TemporalType.TIME)
    private Date timeData;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestampData;

    private String comment;


}

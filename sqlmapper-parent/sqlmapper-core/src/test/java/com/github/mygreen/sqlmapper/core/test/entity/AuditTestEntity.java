package com.github.mygreen.sqlmapper.core.test.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import com.github.mygreen.sqlmapper.core.annotation.CreatedAt;
import com.github.mygreen.sqlmapper.core.annotation.CreatedBy;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;
import com.github.mygreen.sqlmapper.core.annotation.UpdatedAt;
import com.github.mygreen.sqlmapper.core.annotation.UpdatedBy;

import lombok.Data;

@Data
@Entity
@Table(name = "test_audit")
public class AuditTestEntity implements Serializable {

    @Id
    private Long id;

    @CreatedBy
    private String createUser;

    @CreatedAt
    private Timestamp createDatetime;

    @UpdatedBy
    private String updateUser;

    @UpdatedAt
    private Timestamp updateDatetime;

    private String comment;

}

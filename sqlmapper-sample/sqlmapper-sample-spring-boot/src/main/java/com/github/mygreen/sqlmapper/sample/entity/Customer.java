package com.github.mygreen.sqlmapper.sample.entity;

import java.sql.Timestamp;
import java.time.LocalDate;

import com.github.mygreen.sqlmapper.core.annotation.Column;
import com.github.mygreen.sqlmapper.core.annotation.CreatedAt;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.TableGenerator;
import com.github.mygreen.sqlmapper.core.annotation.UpdatedAt;
import com.github.mygreen.sqlmapper.core.annotation.Version;

import lombok.Data;

@Data
@Entity
public class Customer {

    @Id
    @TableGenerator(format = "000")
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "customer_id")
    private String id;

    private String firstName;

    private String lastName;

    private LocalDate birthday;

    @CreatedAt
    private Timestamp createdAt;

    @UpdatedAt
    private Timestamp modifiedAt;

    @Version
    private long version;

}

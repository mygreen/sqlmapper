package com.github.mygreen.sqlmapper.core.testdata;

import java.time.LocalDate;

import com.github.mygreen.sqlmapper.core.annotation.Column;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;

import lombok.Data;

@Entity
@Data
public class Customer {

    @Id
    @Column(name = "customer_id")
    private String id;

    private String firstName;

    private String lastName;

    private LocalDate birthday;

    private long version;

}

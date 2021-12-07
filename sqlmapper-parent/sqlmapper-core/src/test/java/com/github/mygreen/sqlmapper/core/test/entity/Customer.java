package com.github.mygreen.sqlmapper.core.test.entity;

import java.time.LocalDate;

import com.github.mygreen.sqlmapper.core.annotation.Column;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Enumerated;
import com.github.mygreen.sqlmapper.core.annotation.Enumerated.EnumType;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Transient;
import com.github.mygreen.sqlmapper.core.annotation.Version;
import com.github.mygreen.sqlmapper.core.test.entity.type.GenderType;

import lombok.Data;

@Data
@Entity
public class Customer {

    @Id
    @Column(name = "customer_id")
    private String id;

    private String firstName;

    private String lastName;

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    @Version
    private Long version;

    @Transient
    private CustomerAddress address;

}

package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Version;

import lombok.Data;

@Data
@Entity
public class CustomerAddress {

    @Id
    private String customerId;

    private String telNumber;

    private String address;

    @Version
    private Long version;
}

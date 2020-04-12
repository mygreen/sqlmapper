package com.github.mygreen.sqlmapper.testdata;

import com.github.mygreen.sqlmapper.annotation.Entity;
import com.github.mygreen.sqlmapper.annotation.Enumerated;
import com.github.mygreen.sqlmapper.annotation.Enumerated.EnumType;
import com.github.mygreen.sqlmapper.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.annotation.Id;
import com.github.mygreen.sqlmapper.annotation.Version;

import lombok.Data;

@Entity
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Version
    private long version;

}

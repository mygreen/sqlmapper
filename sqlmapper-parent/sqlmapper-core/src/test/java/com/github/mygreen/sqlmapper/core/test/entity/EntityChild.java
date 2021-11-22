package com.github.mygreen.sqlmapper.core.test.entity;

import java.time.LocalDate;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class EntityChild extends EntityBase {

    @Getter
    @Setter
    @Id
    private long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private LocalDate birthday;

}

package com.github.mygreen.sqlmapper.core.test.entity;

import java.time.LocalDate;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 継承したエンティティ
 *
 * @author T.TSUCHIE
 *
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@Table(name="TEST_INHERITANCE")
public class InheritanceTestEntity extends EntityBase {

    @Id
    private long id;

    private String name;

    private LocalDate birthday;

}

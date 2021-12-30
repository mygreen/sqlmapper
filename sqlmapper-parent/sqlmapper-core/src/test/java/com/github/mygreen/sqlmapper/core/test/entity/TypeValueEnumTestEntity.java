package com.github.mygreen.sqlmapper.core.test.entity;

import java.io.Serializable;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Enumerated;
import com.github.mygreen.sqlmapper.core.annotation.Enumerated.EnumType;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Table(name = "test_type_value_enum")
@Entity
public class TypeValueEnumTestEntity implements Serializable {

    @Id
    private long id;

    @Enumerated(EnumType.ORDINAL)
    private EnumTest1Type enumOrdinalData;

    @Enumerated(EnumType.STRING)
    private EnumTest2Type enumNameData;

    private String comment;

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static enum EnumTest1Type {
        ONE(1),
        THOW(2),
        THREE(3);

        @Accessors(fluent = true)
        @Getter
        private final int code;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static enum EnumTest2Type {
        RED(1),
        GREEN(2),
        BLUE(3);

        @Accessors(fluent = true)
        @Getter
        private final int code;
    }
}

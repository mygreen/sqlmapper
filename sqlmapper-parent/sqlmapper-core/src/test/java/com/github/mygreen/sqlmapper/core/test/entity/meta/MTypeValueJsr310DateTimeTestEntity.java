package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.TypeValueJsr310DateTimeTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.LocalDatePath;
import com.github.mygreen.sqlmapper.metamodel.LocalDateTimePath;
import com.github.mygreen.sqlmapper.metamodel.LocalTimePath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MTypeValueJsr310DateTimeTestEntity extends EntityPathBase<TypeValueJsr310DateTimeTestEntity> {

    public static final MTypeValueJsr310DateTimeTestEntity testJsr310DateTime = new MTypeValueJsr310DateTimeTestEntity("testJsr310DateTime");

    public MTypeValueJsr310DateTimeTestEntity(Class<? extends TypeValueJsr310DateTimeTestEntity> type, String name) {
        super(type, name);
    }

    public MTypeValueJsr310DateTimeTestEntity(String name) {
        super(TypeValueJsr310DateTimeTestEntity.class, name);
    }

    public NumberPath<Long> id = createNumber("id", Long.class);

    public LocalDatePath dateData = createLocalDate("dateData");

    public LocalTimePath timeData = createLocalTime("timeData");

    public LocalDateTimePath timestampData = createLocalDateTime("timestampData");

    public StringPath comment = createString("comment");
}

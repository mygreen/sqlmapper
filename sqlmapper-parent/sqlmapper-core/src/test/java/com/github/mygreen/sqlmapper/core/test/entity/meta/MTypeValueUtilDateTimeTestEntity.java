package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.TypeValueUtilDateTimeTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;
import com.github.mygreen.sqlmapper.metamodel.UtilDatePath;

public class MTypeValueUtilDateTimeTestEntity extends EntityPathBase<TypeValueUtilDateTimeTestEntity> {

    public static final MTypeValueUtilDateTimeTestEntity testUtilDateTime = new MTypeValueUtilDateTimeTestEntity("testUtilDateTime");

    public MTypeValueUtilDateTimeTestEntity(Class<? extends TypeValueUtilDateTimeTestEntity> type, String name) {
        super(type, name);
    }

    public MTypeValueUtilDateTimeTestEntity(String name) {
        super(TypeValueUtilDateTimeTestEntity.class, name);
    }

    public NumberPath<Long> id = createNumber("id", Long.class);

    public UtilDatePath dateData = createUtilDate("dateData");

    public UtilDatePath timeData = createUtilDate("timeData");

    public UtilDatePath timestampData = createUtilDate("timestampData");

    public StringPath comment = createString("comment");
}

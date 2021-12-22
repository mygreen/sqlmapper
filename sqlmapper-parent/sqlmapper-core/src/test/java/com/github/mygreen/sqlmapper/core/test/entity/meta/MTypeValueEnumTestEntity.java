package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.TypeValueEnumTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.TypeValueEnumTestEntity.EnumTest1Type;
import com.github.mygreen.sqlmapper.core.test.entity.TypeValueEnumTestEntity.EnumTest2Type;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.EnumPath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MTypeValueEnumTestEntity extends EntityPathBase<TypeValueEnumTestEntity> {

    public static final MTypeValueEnumTestEntity testEnum = new MTypeValueEnumTestEntity("testEnum");

    public MTypeValueEnumTestEntity(Class<? extends TypeValueEnumTestEntity> type, String name) {
        super(type, name);
    }

    public MTypeValueEnumTestEntity(String name) {
        super(TypeValueEnumTestEntity.class, name);
    }

    public NumberPath<Long> id = createNumber("id", Long.class);

    public EnumPath<EnumTest1Type> enumOrdinalData = createEnum("enumOrdinalData", EnumTest1Type.class);

    public EnumPath<EnumTest2Type> enumNameData = createEnum("enumNameData", EnumTest2Type.class);

    public StringPath comment = createString("comment");
}

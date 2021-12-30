package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.TypeValueIntegerNumberTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MTypeValueIntegerNumberTestEntity extends EntityPathBase<TypeValueIntegerNumberTestEntity> {

    public static final MTypeValueIntegerNumberTestEntity testIntegerNumber = new MTypeValueIntegerNumberTestEntity("testIntegerNumber");

    public MTypeValueIntegerNumberTestEntity(Class<? extends TypeValueIntegerNumberTestEntity> type, String name) {
        super(type, name);
    }

    public MTypeValueIntegerNumberTestEntity(String name) {
        super(TypeValueIntegerNumberTestEntity.class, name);
    }

    public NumberPath<Long> id = createNumber("id", Long.class);

    public NumberPath<Short> shortData = createNumber("shortData", Short.class);

    public NumberPath<Integer> intData = createNumber("intData", Integer.class);

    public NumberPath<Long> longData = createNumber("longData", Long.class);

    public StringPath comment = createString("comment");
}

package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.TypeValuePrimitiveNumberTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MTypeValuePrimitiveNumberTestEntity extends EntityPathBase<TypeValuePrimitiveNumberTestEntity> {

    public static final MTypeValuePrimitiveNumberTestEntity testPrimitiveNumber = new MTypeValuePrimitiveNumberTestEntity("testPrimitiveNumber");

    public MTypeValuePrimitiveNumberTestEntity(Class<? extends TypeValuePrimitiveNumberTestEntity> type, String name) {
        super(type, name);
    }

    public MTypeValuePrimitiveNumberTestEntity(String name) {
        super(TypeValuePrimitiveNumberTestEntity.class, name);
    }

    public NumberPath<Long> id = createNumber("id", Long.class);

    public NumberPath<Short> shortData = createNumber("shortData", short.class);

    public NumberPath<Integer> intData = createNumber("intData", int.class);

    public NumberPath<Long> longData = createNumber("longData", long.class);

    public NumberPath<Float> floatData = createNumber("floatData", float.class);

    public NumberPath<Double> doubleData = createNumber("doubleData", double.class);

    public StringPath comment = createString("comment");
}

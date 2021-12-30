package com.github.mygreen.sqlmapper.core.test.entity.meta;

import java.math.BigDecimal;

import com.github.mygreen.sqlmapper.core.test.entity.TypeValueDecimalNumberTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MTypeValueDecimalNumberTestEntity extends EntityPathBase<TypeValueDecimalNumberTestEntity> {

    public static final MTypeValueDecimalNumberTestEntity testDecimalNumber = new MTypeValueDecimalNumberTestEntity("testDecimalNumber");

    public MTypeValueDecimalNumberTestEntity(Class<? extends TypeValueDecimalNumberTestEntity> type, String name) {
        super(type, name);
    }

    public MTypeValueDecimalNumberTestEntity(String name) {
        super(TypeValueDecimalNumberTestEntity.class, name);
    }

    public NumberPath<Long> id = createNumber("id", Long.class);

    public NumberPath<Float> floatData = createNumber("floatData", Float.class);

    public NumberPath<Double> doubleData = createNumber("doubleData", Double.class);

    public NumberPath<BigDecimal> bigdecimalData = createNumber("bigdecimalData", BigDecimal.class);

    public StringPath comment = createString("comment");
}

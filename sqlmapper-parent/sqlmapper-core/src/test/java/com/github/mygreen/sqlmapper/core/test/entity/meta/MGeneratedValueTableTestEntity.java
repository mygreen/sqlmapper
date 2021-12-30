package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueTableTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MGeneratedValueTableTestEntity extends EntityPathBase<GeneratedValueTableTestEntity> {

    public static final MGeneratedValueTableTestEntity testTable = new MGeneratedValueTableTestEntity("testTable");

    public MGeneratedValueTableTestEntity(Class<? extends GeneratedValueTableTestEntity> type, String name) {
        super(type, name);
    }

    public MGeneratedValueTableTestEntity(String name) {
        super(GeneratedValueTableTestEntity.class, name);
    }

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath comment = createString("comment");
}

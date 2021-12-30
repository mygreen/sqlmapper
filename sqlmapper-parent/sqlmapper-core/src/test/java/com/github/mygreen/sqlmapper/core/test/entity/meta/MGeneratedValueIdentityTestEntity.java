package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueIdentityTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MGeneratedValueIdentityTestEntity extends EntityPathBase<GeneratedValueIdentityTestEntity> {

    public static final MGeneratedValueIdentityTestEntity testIdentity = new MGeneratedValueIdentityTestEntity("testIdentity");

    public MGeneratedValueIdentityTestEntity(Class<? extends GeneratedValueIdentityTestEntity> type, String name) {
        super(type, name);
    }

    public MGeneratedValueIdentityTestEntity(String name) {
        super(GeneratedValueIdentityTestEntity.class, name);
    }

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath comment = createString("comment");
}

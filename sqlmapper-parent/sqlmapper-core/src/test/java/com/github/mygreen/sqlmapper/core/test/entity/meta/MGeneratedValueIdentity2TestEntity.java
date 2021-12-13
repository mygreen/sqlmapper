package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueIdentity2TestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MGeneratedValueIdentity2TestEntity extends EntityPathBase<GeneratedValueIdentity2TestEntity> {

    public static final MGeneratedValueIdentity2TestEntity testIdentity2 = new MGeneratedValueIdentity2TestEntity("testIdentity2");

    public MGeneratedValueIdentity2TestEntity(Class<? extends GeneratedValueIdentity2TestEntity> type, String name) {
        super(type, name);
    }

    public MGeneratedValueIdentity2TestEntity(String name) {
        super(GeneratedValueIdentity2TestEntity.class, name);
    }

    public final NumberPath<Long> id1 = createNumber("id1", Long.class);

    public final NumberPath<Integer> id2 = createNumber("id2", Integer.class);

    public final StringPath comment = createString("comment");
}

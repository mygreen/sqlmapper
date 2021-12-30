package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueSequenceTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MGeneratedValueSequenceTestEntity extends EntityPathBase<GeneratedValueSequenceTestEntity> {

    public static final MGeneratedValueSequenceTestEntity testSequence = new MGeneratedValueSequenceTestEntity("testSequence");

    public MGeneratedValueSequenceTestEntity(Class<? extends GeneratedValueSequenceTestEntity> type, String name) {
        super(type, name);
    }

    public MGeneratedValueSequenceTestEntity(String name) {
        super(GeneratedValueSequenceTestEntity.class, name);
    }

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath comment = createString("comment");
}

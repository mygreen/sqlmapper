package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueSequenceFormatTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MGeneratedValueSequenceFormatTestEntity extends EntityPathBase<GeneratedValueSequenceFormatTestEntity> {

    public static final MGeneratedValueSequenceFormatTestEntity testSequenceFormat = new MGeneratedValueSequenceFormatTestEntity("testSequenceFormat");

    public MGeneratedValueSequenceFormatTestEntity(Class<? extends GeneratedValueSequenceFormatTestEntity> type, String name) {
        super(type, name);
    }

    public MGeneratedValueSequenceFormatTestEntity(String name) {
        super(GeneratedValueSequenceFormatTestEntity.class, name);
    }

    public final StringPath id = createString("id");

    public final StringPath comment = createString("comment");
}

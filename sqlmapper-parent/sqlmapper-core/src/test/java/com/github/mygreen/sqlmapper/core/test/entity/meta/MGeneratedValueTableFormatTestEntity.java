package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueTableFormatTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MGeneratedValueTableFormatTestEntity extends EntityPathBase<GeneratedValueTableFormatTestEntity> {

    public static final MGeneratedValueTableFormatTestEntity testTableFormat = new MGeneratedValueTableFormatTestEntity("testTableFormat");

    public MGeneratedValueTableFormatTestEntity(Class<? extends GeneratedValueTableFormatTestEntity> type, String name) {
        super(type, name);
    }

    public MGeneratedValueTableFormatTestEntity(String name) {
        super(GeneratedValueTableFormatTestEntity.class, name);
    }

    public final StringPath id = createString("id");

    public final StringPath comment = createString("comment");
}

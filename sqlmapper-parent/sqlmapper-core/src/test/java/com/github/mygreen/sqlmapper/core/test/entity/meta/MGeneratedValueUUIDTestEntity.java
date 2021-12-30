package com.github.mygreen.sqlmapper.core.test.entity.meta;

import java.util.UUID;

import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueUUIDTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.GeneralPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MGeneratedValueUUIDTestEntity extends EntityPathBase<GeneratedValueUUIDTestEntity> {

    public static final MGeneratedValueUUIDTestEntity testUUID = new MGeneratedValueUUIDTestEntity("testUUID");

    public MGeneratedValueUUIDTestEntity(Class<? extends GeneratedValueUUIDTestEntity> type, String name) {
        super(type, name);
    }

    public MGeneratedValueUUIDTestEntity(String name) {
        super(GeneratedValueUUIDTestEntity.class, name);
    }

    public final GeneralPath<UUID> id = createGeneral("id", UUID.class);

    public final StringPath comment = createString("comment");
}

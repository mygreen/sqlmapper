package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.EmbeddedTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.EmbeddedTestEntity.PK;
import com.github.mygreen.sqlmapper.metamodel.BooleanPath;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MEmbeddedTestEntity extends EntityPathBase<EmbeddedTestEntity> {

    public static final MEmbeddedTestEntity testEmbedded = new MEmbeddedTestEntity("embeddedEntity");

    public final MPK id = new MPK(this, "id");

    public final StringPath name = createString("name");

    public final BooleanPath deleted = createBoolean("deleted");

    public MEmbeddedTestEntity(Class<? extends EmbeddedTestEntity> type, String name) {
        super(type, name);
    }

    public MEmbeddedTestEntity(String name) {
        super(EmbeddedTestEntity.class, name);
    }

    public static class MPK extends EntityPathBase<PK> {

        public final StringPath key1 = createString("key1");

        public final NumberPath<Long> key2 = createNumber("key2", Long.class);

        public MPK(Class<? extends PK> type, EntityPathBase<?> parent, String name) {
            super(type, parent, name);
        }

        public MPK(EntityPathBase<?> parent, String name) {
            super(PK.class, parent, name);
        }

    }
}

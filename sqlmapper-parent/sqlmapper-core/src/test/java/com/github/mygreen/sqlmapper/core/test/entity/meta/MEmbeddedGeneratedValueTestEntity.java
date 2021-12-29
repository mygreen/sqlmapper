package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.EmbeddedGeneratedValueTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.EmbeddedTestEntity.PK;
import com.github.mygreen.sqlmapper.metamodel.BooleanPath;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MEmbeddedGeneratedValueTestEntity extends EntityPathBase<EmbeddedGeneratedValueTestEntity> {

    public static final MEmbeddedGeneratedValueTestEntity testEmbeddedGeneratedValue = new MEmbeddedGeneratedValueTestEntity("testEmbeddedGeneratedValue");

    public final MPK id = new MPK(this, "id");

    public final StringPath name = createString("name");

    public final BooleanPath deleted = createBoolean("deleted");

    public MEmbeddedGeneratedValueTestEntity(Class<? extends EmbeddedGeneratedValueTestEntity> type, String name) {
        super(type, name);
    }

    public MEmbeddedGeneratedValueTestEntity(String name) {
        super(EmbeddedGeneratedValueTestEntity.class, name);
    }

    public static class MPK extends EntityPathBase<PK> {

        public final NumberPath<Long> key1 = createNumber("key1", Long.class);

        public final StringPath key2 = createString("key2");

        public final StringPath key3 = createString("key3");


        public MPK(Class<? extends PK> type, EntityPathBase<?> parent, String name) {
            super(type, parent, name);
        }

        public MPK(EntityPathBase<?> parent, String name) {
            super(PK.class, parent, name);
        }

    }
}

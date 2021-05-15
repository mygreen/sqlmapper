package com.github.mygreen.sqlmapper.core.testdata;

import com.github.mygreen.sqlmapper.core.testdata.EmbeddedEntity.PK;
import com.github.mygreen.sqlmapper.metamodel.BooleanPath;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MEmbeddedEntity extends EntityPathBase<EmbeddedEntity> {

    public static final MEmbeddedEntity embeddedEntity = new MEmbeddedEntity("embeddedEntity");

    public final MPK id = new MPK(this, "id");

    public final StringPath name = createString("name");

    public final BooleanPath deleted = createBoolean("deleted");

    public MEmbeddedEntity(Class<? extends EmbeddedEntity> type, String name) {
        super(type, name);
    }

    public MEmbeddedEntity(String name) {
        super(EmbeddedEntity.class, name);
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

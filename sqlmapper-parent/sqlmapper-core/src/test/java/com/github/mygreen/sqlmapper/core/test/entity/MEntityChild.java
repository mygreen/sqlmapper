package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.metamodel.LocalDatePath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MEntityChild extends MEntityBase<EntityChild> {

    public static final MEntityChild entityChild = new MEntityChild("entityChild");

    public MEntityChild(Class<? extends EntityChild> type, String name) {
        super(type, name);
    }

    public MEntityChild(String name) {
        super(EntityChild.class, name);
    }

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final LocalDatePath birthday = createLocalDate("birthday");

}

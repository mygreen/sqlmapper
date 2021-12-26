package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.InheritanceTestEntity;
import com.github.mygreen.sqlmapper.metamodel.LocalDatePath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MInheritanceTestEntity extends MEntityBase<InheritanceTestEntity> {

    public static final MInheritanceTestEntity testInheritance = new MInheritanceTestEntity("testInheritance");

    public MInheritanceTestEntity(Class<? extends InheritanceTestEntity> type, String name) {
        super(type, name);
    }

    public MInheritanceTestEntity(String name) {
        super(InheritanceTestEntity.class, name);
    }

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final LocalDatePath birthday = createLocalDate("birthday");

}

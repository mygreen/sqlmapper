package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MSection extends EntityPathBase<Section> {

    public static final MSection section = new MSection("section");

    public MSection(Class<? extends Section> type, String name) {
        super(type, name);
    }

    public MSection(String name) {
        super(Section.class, name);
    }

    public final StringPath code = createString("code");

    public final NumberPath<Integer> businessEstablishmentCode = createNumber("businessEstablishmentCode", Integer.class);

    public final StringPath name = createString("name");

}

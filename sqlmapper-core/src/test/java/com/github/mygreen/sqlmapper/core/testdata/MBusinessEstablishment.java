package com.github.mygreen.sqlmapper.core.testdata;

import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MBusinessEstablishment extends EntityPathBase<BusinessEstablishment> {

    public static final MBusinessEstablishment businessEstablishment = new MBusinessEstablishment("businessEstablishment");

    public MBusinessEstablishment(Class<? extends BusinessEstablishment> type, String name) {
        super(type, name);
    }

    public MBusinessEstablishment(String name) {
        super(BusinessEstablishment.class, name);
    }

    public final NumberPath<Integer> code = createNumber("code", Integer.class);

    public final StringPath name = createString("name");

    public final StringPath address = createString("address");

    public final StringPath tel = createString("tel");




}

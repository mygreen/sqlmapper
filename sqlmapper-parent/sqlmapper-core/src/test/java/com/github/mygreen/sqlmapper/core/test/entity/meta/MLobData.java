package com.github.mygreen.sqlmapper.core.test.entity.meta;

import java.util.UUID;

import com.github.mygreen.sqlmapper.core.test.entity.LobData;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.GeneralPath;

public class MLobData extends EntityPathBase<LobData> {

    public static final MLobData lobData = new MLobData("lobData");

    public MLobData(Class<? extends LobData> type, String name) {
        super(type, name);
    }

    public MLobData(String name) {
        super(LobData.class, name);
    }

    public GeneralPath<UUID> id = createGeneral("id", UUID.class);

    public GeneralPath<byte[]> blob = createGeneral("blob", byte[].class);

}

package com.github.mygreen.sqlmapper.core.test.entity.meta;

import java.sql.Blob;
import java.sql.Clob;

import com.github.mygreen.sqlmapper.core.test.entity.TypeValueLobTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.GeneralPath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MTypeValueLobTestEntity extends EntityPathBase<TypeValueLobTestEntity> {

    public static final MTypeValueLobTestEntity testLob = new MTypeValueLobTestEntity("testLob");

    public MTypeValueLobTestEntity(Class<? extends TypeValueLobTestEntity> type, String name) {
        super(type, name);
    }

    public MTypeValueLobTestEntity(String name) {
        super(TypeValueLobTestEntity.class, name);
    }

    public NumberPath<Long> id = createNumber("id", Long.class);

    public GeneralPath<Clob> clobData1 = createGeneral("clobData1", Clob.class);

    public GeneralPath<String> clobData2 = createGeneral("clobData2", String.class);

    public GeneralPath<Blob> blobData1 = createGeneral("blobData1", Blob.class);

    public GeneralPath<byte[]> blobData2 = createGeneral("blobData2", byte[].class);

    public StringPath comment = createString("comment");
}

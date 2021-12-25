package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.AuditTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.SqlTimestampPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MAuditTestEntity extends EntityPathBase<AuditTestEntity> {

    public static final MAuditTestEntity testAudit = new MAuditTestEntity("testAudit");

    public MAuditTestEntity(Class<? extends AuditTestEntity> type, String name) {
        super(type, name);
    }

    public MAuditTestEntity(String name) {
        super(AuditTestEntity.class, name);
    }

    public NumberPath<Long> id = createNumber("id", Long.class);

    public StringPath createUser = createString("createUser");

    public SqlTimestampPath createDatetime = createSqlTimestamp("createDatetime");

    public StringPath updateUser = createString("updateUser");

    public SqlTimestampPath updateDatetime = createSqlTimestamp("updateDatetime");

    public StringPath comment = createString("comment");
}

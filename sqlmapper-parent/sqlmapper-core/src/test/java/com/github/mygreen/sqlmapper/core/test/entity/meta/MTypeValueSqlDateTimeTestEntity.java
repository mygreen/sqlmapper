package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.TypeValueSqlDateTimeTestEntity;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.SqlDatePath;
import com.github.mygreen.sqlmapper.metamodel.SqlTimePath;
import com.github.mygreen.sqlmapper.metamodel.SqlTimestampPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MTypeValueSqlDateTimeTestEntity extends EntityPathBase<TypeValueSqlDateTimeTestEntity> {

    public static final MTypeValueSqlDateTimeTestEntity testSqlDateTime = new MTypeValueSqlDateTimeTestEntity("testSqlDateTime");

    public MTypeValueSqlDateTimeTestEntity(Class<? extends TypeValueSqlDateTimeTestEntity> type, String name) {
        super(type, name);
    }

    public MTypeValueSqlDateTimeTestEntity(String name) {
        super(TypeValueSqlDateTimeTestEntity.class, name);
    }

    public NumberPath<Long> id = createNumber("id", Long.class);

    public SqlDatePath dateData = createSqlDate("dateData");

    public SqlTimePath timeData = createSqlTime("timeData");

    public SqlTimestampPath timestampData = createSqlTimestamp("timestampData");

    public StringPath comment = createString("comment");
}

package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.github.mygreen.sqlmapper.annotation.Temporal.TemporalType;

public class SqlTimestampType implements SqlTemporalType<Timestamp> {

    private final String pattern;

    public SqlTimestampType() {
        this("yyyy-MM-dd HH:mm:ss.SSS");
    }

    public SqlTimestampType(final String pattern) {
        this.pattern = pattern;
    }

    @Override
    public TemporalType getTemporalType() {
        return TemporalType.TIMESTAMP;
    }

    @Override
    public Timestamp getValue(ResultSet rs, int columnIndex) throws SQLException {

        Timestamp value = rs.getTimestamp(columnIndex);
        if(rs.wasNull()) {
            return null;
        }

        return value;
    }

    @Override
    public Object getSqlParameterValue(Timestamp value) {
        return value;
    }

    @Override
    public String getAsText(Timestamp value) {
        return new SimpleDateFormat(pattern).format(value);
    }
}

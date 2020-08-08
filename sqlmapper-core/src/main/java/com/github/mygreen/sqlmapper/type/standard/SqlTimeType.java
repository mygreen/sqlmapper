package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;

import com.github.mygreen.sqlmapper.annotation.Temporal.TemporalType;

public class SqlTimeType implements SqlTemporalType<Time> {

    private final String pattern;

    public SqlTimeType() {
        this("yyyy-MM-dd");
    }

    public SqlTimeType(final String pattern) {
        this.pattern = pattern;
    }

    @Override
    public TemporalType getTemporalType() {
        return TemporalType.TIME;
    }

    @Override
    public Time getValue(ResultSet rs, int columnIndex) throws SQLException {

        Time value = rs.getTime(columnIndex);
        if(rs.wasNull()) {
            return null;
        }

        return value;
    }

    @Override
    public Object getSqlParameterValue(Time value) {
        return value;
    }

    @Override
    public String getEmbeddedValue(Time value) {
        return value != null ? new SimpleDateFormat(pattern).format(value) : null;
    }
}

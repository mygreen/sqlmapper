package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.github.mygreen.sqlmapper.core.annotation.Temporal.TemporalType;

public class SqlDateType implements SqlTemporalType<Date> {

    private final String pattern;

    public SqlDateType() {
        this("yyyy-MM-dd");
    }

    public SqlDateType(final String pattern) {
        this.pattern = pattern;
    }

    @Override
    public TemporalType getTemporalType() {
        return TemporalType.DATE;
    }

    @Override
    public Date getValue(ResultSet rs, int columnIndex) throws SQLException {

        Date value = rs.getDate(columnIndex);
        if(rs.wasNull()) {
            return null;
        }

        return value;
    }

    @Override
    public Object getSqlParameterValue(Date value) {
        return value;
    }

    @Override
    public String getEmbeddedValue(Date value) {
        return value != null ? new SimpleDateFormat(pattern).format(value) : null;
    }

}

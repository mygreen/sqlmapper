package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

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
    public void bindValue(Time value, MapSqlParameterSource paramSource, String paramName) {

        paramSource.addValue(paramName, value);
    }

    @Override
    public String getAsText(Time value) {
        return new SimpleDateFormat(pattern).format(value);
    }
}

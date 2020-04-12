package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

public class LocalTimeType implements ValueType<LocalTime> {

    @Override
    public LocalTime getValue(ResultSet rs, int columnIndex) throws SQLException {

        Time value = rs.getTime(columnIndex);
        if(value == null) {
            return null;
        }

        return value.toLocalTime();
    }

    @Override
    public void bindValue(LocalTime value, MapSqlParameterSource paramSource, String paramName) {
        Time sqlValue = (value != null ? Time.valueOf(value) : null);
        paramSource.addValue(paramName, sqlValue);
    }
}

package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

public class LocalDateTimeType implements ValueType<LocalDateTime> {

    @Override
    public LocalDateTime getValue(ResultSet rs, int columnIndex) throws SQLException {

        Timestamp value = rs.getTimestamp(columnIndex);
        if(value == null) {
            return null;
        }

        return value.toLocalDateTime();
    }

    @Override
    public void bindValue(LocalDateTime value, MapSqlParameterSource paramSource, String paramName) {
        Timestamp sqlValue = (value != null ? Timestamp.valueOf(value) : null);
        paramSource.addValue(paramName, sqlValue);
    }
}

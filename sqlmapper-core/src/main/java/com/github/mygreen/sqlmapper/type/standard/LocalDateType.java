package com.github.mygreen.sqlmapper.type.standard;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

public class LocalDateType implements ValueType<LocalDate> {

    @Override
    public LocalDate getValue(ResultSet rs, int columnIndex) throws SQLException {

        Date value = rs.getDate(columnIndex);
        if(value == null) {
            return null;
        }

        return value.toLocalDate();
    }

    @Override
    public void bindValue(LocalDate value, MapSqlParameterSource paramSource, String paramName) {
        Date sqlValue = (value != null ? Date.valueOf(value) : null);
        paramSource.addValue(paramName, sqlValue);
    }
}

package com.github.mygreen.sqlmapper.type.standard;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.annotation.Temporal.TemporalType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SqlDateType implements SqlTemporalType<Date> {

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
    public void bindValue(Date value, MapSqlParameterSource paramSource, String paramName) {

        paramSource.addValue(paramName, value);
    }

}

package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UtilDateType implements ValueType<Date> {

    @SuppressWarnings("rawtypes")
    private final SqlTemporalType temporalConverter;

    @Override
    public Date getValue(ResultSet rs, int columnIndex) throws SQLException {
        return (Date)temporalConverter.getValue(rs, columnIndex);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bindValue(Date value, MapSqlParameterSource paramSource, String paramName) {
        temporalConverter.bindValue(value, paramSource, paramName);
    }
}

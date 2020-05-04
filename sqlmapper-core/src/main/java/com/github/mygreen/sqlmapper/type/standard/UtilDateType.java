package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

public class UtilDateType implements ValueType<Date> {

    @SuppressWarnings("rawtypes")
    private final SqlTemporalType temporalConverter;

    private final String pattern;

    @SuppressWarnings("rawtypes")
    public UtilDateType(final SqlTemporalType temporalConverter) {
        this(temporalConverter, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    @SuppressWarnings("rawtypes")
    public UtilDateType(SqlTemporalType temporalConverter, final String pattern) {
        this.temporalConverter = temporalConverter;
        this.pattern = pattern;
    }

    @Override
    public Date getValue(ResultSet rs, int columnIndex) throws SQLException {
        return (Date)temporalConverter.getValue(rs, columnIndex);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bindValue(Date value, MapSqlParameterSource paramSource, String paramName) {
        temporalConverter.bindValue(value, paramSource, paramName);
    }

    @Override
    public String getAsText(Date value) {
        return new SimpleDateFormat(pattern).format(value);
    }
}

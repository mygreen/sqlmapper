package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.mygreen.sqlmapper.core.type.ValueType;

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
    public Object getSqlParameterValue(Date value) {
        return  temporalConverter.getSqlParameterValue(value);
    }

    @Override
    public String getEmbeddedValue(Date value) {
        return value != null ? new SimpleDateFormat(pattern).format(value) : null;
    }
}

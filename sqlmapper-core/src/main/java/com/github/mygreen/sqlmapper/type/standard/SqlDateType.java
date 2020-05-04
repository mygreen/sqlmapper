package com.github.mygreen.sqlmapper.type.standard;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.annotation.Temporal.TemporalType;

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
    public void bindValue(Date value, MapSqlParameterSource paramSource, String paramName) {

        paramSource.addValue(paramName, value);
    }

    @Override
    public String getAsText(Date value) {
        return new SimpleDateFormat(pattern).format(value);
    }

}

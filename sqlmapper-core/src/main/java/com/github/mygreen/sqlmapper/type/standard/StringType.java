package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.mygreen.sqlmapper.type.ValueType;

public class StringType implements ValueType<String> {

    @Override
    public String getValue(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getString(columnIndex);
    }

    @Override
    public Object getSqlParameterValue(String value) {
        return value;
    }
}

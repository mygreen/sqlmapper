package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BooleanType implements ValueType<Boolean> {

    /**
     * プリミティブ型かどうか
     */
    private final boolean forPrimitive;

    @Override
    public Boolean getValue(ResultSet rs, int columnIndex) throws SQLException {

        Boolean value = rs.getBoolean(columnIndex);
        if(rs.wasNull() && !forPrimitive) {
            return null;
        }

        return value;
    }

    @Override
    public void bindValue(Boolean value, MapSqlParameterSource paramSource, String paramName) {

        if(value == null && forPrimitive) {
            value = false;
        }

        paramSource.addValue(paramName, value);
    }
}

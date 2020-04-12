package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShortType implements ValueType<Short> {

    /**
     * プリミティブ型かどうか
     */
    private final boolean forPrimitive;

    @Override
    public Short getValue(ResultSet rs, int columnIndex) throws SQLException {

        Short value = rs.getShort(columnIndex);
        if(rs.wasNull() && !forPrimitive) {
            return null;
        }

        return value;
    }

    @Override
    public void bindValue(Short value, MapSqlParameterSource paramSource, String paramName) {

        if(value == null && forPrimitive) {
            value = 0;
        }

        paramSource.addValue(paramName, value);
    }
}

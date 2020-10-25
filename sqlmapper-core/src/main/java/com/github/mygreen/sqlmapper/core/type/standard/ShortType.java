package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.mygreen.sqlmapper.core.type.ValueType;

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
    public Object getSqlParameterValue(Short value) {

        if(value == null && forPrimitive) {
            value = 0;
        }

        return value;
    }
}

package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LongType implements ValueType<Long> {

    /**
     * プリミティブ型かどうか
     */
    private final boolean forPrimitive;

    @Override
    public Long getValue(ResultSet rs, int columnIndex) throws SQLException {

        Long value = rs.getLong(columnIndex);
        if(rs.wasNull() && !forPrimitive) {
            return null;
        }

        return value;
    }

    @Override
    public Object getSqlParameterValue(Long value) {

        if(value == null && forPrimitive) {
            value = 0L;
        }

        return value;
    }
}

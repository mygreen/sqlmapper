package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FloatType implements ValueType<Float> {

    /**
     * プリミティブ型かどうか
     */
    private final boolean forPrimitive;

    @Override
    public Float getValue(ResultSet rs, int columnIndex) throws SQLException {

        Float value = rs.getFloat(columnIndex);
        if(rs.wasNull() && !forPrimitive) {
            return null;
        }

        return value;
    }

    @Override
    public Object getSqlParameterValue(Float value) {

        if(value == null && forPrimitive) {
            value = 0.0f;
        }

        return value;
    }
}

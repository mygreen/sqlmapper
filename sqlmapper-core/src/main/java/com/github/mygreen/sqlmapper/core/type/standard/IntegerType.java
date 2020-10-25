package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IntegerType implements ValueType<Integer> {

    /**
     * プリミティブ型かどうか
     */
    private final boolean forPrimitive;

    @Override
    public Integer getValue(ResultSet rs, int columnIndex) throws SQLException {

        Integer value = rs.getInt(columnIndex);
        if(rs.wasNull() && !forPrimitive) {
            return null;
        }

        return value;
    }

    @Override
    public Object getSqlParameterValue(Integer value) {

        if(value == null && forPrimitive) {
            value = 0;
        }

        return value;
    }
}

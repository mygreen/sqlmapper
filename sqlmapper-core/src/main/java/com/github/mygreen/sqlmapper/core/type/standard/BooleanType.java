package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BooleanType implements ValueType<Boolean> {

    /**
     * プリミティブ型かどうか
     */
    @Getter
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
    public Object getSqlParameterValue(Boolean value) {

        if(value == null && forPrimitive) {
            value = false;
        }

        return value;
    }
}

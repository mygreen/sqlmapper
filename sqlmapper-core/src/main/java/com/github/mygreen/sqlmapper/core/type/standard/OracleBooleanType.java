package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OracleBooleanType implements ValueType<Boolean> {

    /**
     * プリミティブ型かどうか
     */
    @Getter
    private final boolean forPrimitive;

    @Override
    public Boolean getValue(ResultSet rs, int columnIndex) throws SQLException {

        int value = rs.getInt(columnIndex);
        if(rs.wasNull() && !forPrimitive) {
            return null;
        }

        return value > 0 ? true : false;
    }

    @Override
    public Object getSqlParameterValue(Boolean value) {

        if(value == null && forPrimitive) {
            value = false;
        }

        final int sqlValue = value ? 1 : 0;
        return sqlValue;
    }
}

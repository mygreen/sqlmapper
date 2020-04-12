package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OracleBooleanType implements ValueType<Boolean> {

    /**
     * プリミティブ型かどうか
     */
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
    public void bindValue(Boolean value, MapSqlParameterSource paramSource, String paramName) {

        if(value == null && forPrimitive) {
            value = false;
        }

        final int sqlValue = value ? 1 : 0;
        paramSource.addValue(paramName, sqlValue);
    }
}

package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

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
    public void bindValue(Float value, MapSqlParameterSource paramSource, String paramName) {

        if(value == null && forPrimitive) {
            value = 0.0f;
        }

        paramSource.addValue(paramName, value);
    }
}

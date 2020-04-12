package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DoubleType implements ValueType<Double> {

    /**
     * プリミティブ型かどうか
     */
    private final boolean forPrimitive;

    @Override
    public Double getValue(ResultSet rs, int columnIndex) throws SQLException {

        Double value = rs.getDouble(columnIndex);
        if(rs.wasNull() && !forPrimitive) {
            return null;
        }

        return value;
    }

    @Override
    public void bindValue(Double value, MapSqlParameterSource paramSource, String paramName) {

        if(value == null && forPrimitive) {
            value = 0.0d;
        }

        paramSource.addValue(paramName, value);
    }
}

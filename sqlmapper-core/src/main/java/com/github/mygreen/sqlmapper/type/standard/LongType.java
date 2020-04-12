package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

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
    public void bindValue(Long value, MapSqlParameterSource paramSource, String paramName) {

        if(value == null && forPrimitive) {
            value = 0L;
        }

        paramSource.addValue(paramName, value);
    }
}

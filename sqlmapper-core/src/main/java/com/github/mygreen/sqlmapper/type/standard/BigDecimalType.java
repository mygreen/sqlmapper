package com.github.mygreen.sqlmapper.type.standard;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BigDecimalType implements ValueType<BigDecimal> {

    @Override
    public BigDecimal getValue(ResultSet rs, int columnIndex) throws SQLException {

        BigDecimal value = rs.getBigDecimal(columnIndex);
        if(value == null) {
            return null;
        }

        return value;
    }

    @Override
    public void bindValue(BigDecimal value, MapSqlParameterSource paramSource, String paramName) {

        paramSource.addValue(paramName, value);
    }
}

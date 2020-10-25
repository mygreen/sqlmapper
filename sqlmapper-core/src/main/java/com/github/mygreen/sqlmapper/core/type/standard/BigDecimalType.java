package com.github.mygreen.sqlmapper.type.standard;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public Object getSqlParameterValue(BigDecimal value) {
        return value;
    }

    @Override
    public String getEmbeddedValue(BigDecimal value) {
        return value != null ? value.toPlainString() : null;
    }

}

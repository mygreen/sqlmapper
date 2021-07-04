package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.RequiredArgsConstructor;

/**
 * double 型及びそのラッパー型のマッピングを処理します。
 *
 *
 * @author T.TSUCHIE
 *
 */
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

    /**
     * {@inheritDoc}
     * @return 引数で指定した値が{@literal null} かつ、プリミティブ型にマッピングする際は {@literal 0.0d} を返します。
     */
    @Override
    public Object getSqlParameterValue(Double value) {

        if(value == null && forPrimitive) {
            value = 0.0d;
        }

        return value;
    }
}

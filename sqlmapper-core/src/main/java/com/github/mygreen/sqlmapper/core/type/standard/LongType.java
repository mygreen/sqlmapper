package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.RequiredArgsConstructor;

/**
 * long 型及びそのラッパー型のマッピングを処理します。
 *
 *
 * @author T.TSUCHIE
 *
 */
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

    /**
     * {@inheritDoc}
     * @return 引数で指定した値が{@literal null} かつ、プリミティブ型にマッピングする際は {@literal 0L} を返します。
     */
    @Override
    public Object getSqlParameterValue(Long value) {

        if(value == null && forPrimitive) {
            value = 0L;
        }

        return value;
    }
}

package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.RequiredArgsConstructor;

/**
 * int 型及びそのラッパー型のマッピングを処理します。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class IntegerType implements ValueType<Integer> {

    /**
     * プリミティブ型かどうか
     */
    private final boolean forPrimitive;

    @Override
    public Integer getValue(ResultSet rs, int columnIndex) throws SQLException {

        Integer value = rs.getInt(columnIndex);
        if(rs.wasNull() && !forPrimitive) {
            return null;
        }

        return value;
    }

    /**
     * {@inheritDoc}
     * @return 引数で指定した値が{@literal null} かつ、プリミティブ型にマッピングする際は {@literal 0} を返します。
     */
    @Override
    public Object getSqlParameterValue(Integer value) {

        if(value == null && forPrimitive) {
            value = 0;
        }

        return value;
    }
}

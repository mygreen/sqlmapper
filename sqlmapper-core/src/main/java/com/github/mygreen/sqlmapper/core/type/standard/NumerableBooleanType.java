package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 数値をブール値にマッピングして処理を行います。
 * <ul>
 *  <li>DB値 0 => false にマッピング</li>
 *  <li>DB値 1 => true にマッピング</li>
 * </ul>
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class NumerableBooleanType implements ValueType<Boolean> {

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

    /**
     * {@inheritDoc}
     * @return 引数で指定した値が{@literal null} かつ、プリミティブ型にマッピングする際は {@literal false} を返します。
     */
    @Override
    public Object getSqlParameterValue(Boolean value) {

        if(value == null && forPrimitive) {
            value = false;
        }

        final int sqlValue = value ? 1 : 0;
        return sqlValue;
    }
}

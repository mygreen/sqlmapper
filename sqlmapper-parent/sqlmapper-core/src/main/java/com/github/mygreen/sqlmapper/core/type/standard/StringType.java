package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.type.ValueType;

/**
 * {@link String} 型のマッピングを処理します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class StringType implements ValueType<String> {

    @Override
    public String getValue(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getString(columnIndex);
    }

    @Override
    public Object getSqlParameterValue(String value) {
        return value;
    }

    @Override
    public int getSqlType(Dialect dialect) {
        return Types.VARCHAR;
    }
}

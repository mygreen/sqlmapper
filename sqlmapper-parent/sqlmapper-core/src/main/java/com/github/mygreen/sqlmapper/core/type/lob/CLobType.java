package com.github.mygreen.sqlmapper.core.type.lob;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.type.SqlParameterBindException;
import com.github.mygreen.sqlmapper.core.type.SqlValueConversionException;
import com.github.mygreen.sqlmapper.core.type.ValueType;


/**
 * {@link Clob}型のマッピングを処理します。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class CLobType implements ValueType<Clob> {

    @Override
    public int getSqlType(Dialect dialect) {
        return Types.CLOB;
    }

    @Override
    public Clob getValue(ResultSet rs, int columnIndex)
            throws SQLException, SqlValueConversionException {
        return rs.getClob(columnIndex);
    }

    @Override
    public Object getSqlParameterValue(Clob value) throws SqlParameterBindException {
        return value;
    }
}

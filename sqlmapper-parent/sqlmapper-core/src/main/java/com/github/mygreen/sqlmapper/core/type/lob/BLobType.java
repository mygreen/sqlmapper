package com.github.mygreen.sqlmapper.core.type.lob;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.type.SqlParameterBindException;
import com.github.mygreen.sqlmapper.core.type.SqlValueConversionException;
import com.github.mygreen.sqlmapper.core.type.ValueType;


/**
 * {@link Blob}型のマッピングを処理します。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */

public class BLobType implements ValueType<Blob> {

    @Override
    public int getSqlType(Dialect dialect) {
        return Types.BLOB;
    }

    @Override
    public Blob getValue(ResultSet rs, int columnIndex)
            throws SQLException, SqlValueConversionException {
        return rs.getBlob(columnIndex);
    }

    @Override
    public Object getSqlParameterValue(Blob value) throws SqlParameterBindException {
        return value;
    }
}

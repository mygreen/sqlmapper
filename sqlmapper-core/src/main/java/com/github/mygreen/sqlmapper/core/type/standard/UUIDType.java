package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.springframework.jdbc.core.SqlParameterValue;

import com.github.mygreen.sqlmapper.core.type.ValueType;

/**
 * {@link UUID} 型のマッピングを処理します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class UUIDType implements ValueType<UUID> {

    @Override
    public UUID getValue(ResultSet rs, int columnIndex) throws SQLException {
        return (UUID)rs.getObject(columnIndex);
    }

    @Override
    public Object getSqlParameterValue(UUID value) {
        return new SqlParameterValue(Types.OTHER, value);
    }
}

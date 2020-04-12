package com.github.mygreen.sqlmapper.type.lob;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.LobHandler;

import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LobByteArrayType implements ValueType<byte[]> {

    private final LobHandler lobHandler;

    @Override
    public byte[] getValue(ResultSet rs, int columnIndex) throws SQLException {
        return lobHandler.getBlobAsBytes(rs, columnIndex);
    }

    @Override
    public void bindValue(byte[] value, MapSqlParameterSource paramSource, String paramName) {
        paramSource.addValue(paramName, new SqlParameterValue(Types.BLOB, new SqlLobValue(value, lobHandler)));
    }
}

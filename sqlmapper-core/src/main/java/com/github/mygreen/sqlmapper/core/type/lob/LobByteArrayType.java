package com.github.mygreen.sqlmapper.core.type.lob;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.LobHandler;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LobByteArrayType implements ValueType<byte[]> {

    private final LobHandler lobHandler;

    @Override
    public byte[] getValue(ResultSet rs, int columnIndex) throws SQLException {
        return lobHandler.getBlobAsBytes(rs, columnIndex);
    }

    @Override
    public Object getSqlParameterValue(byte[] value) {
        return new SqlParameterValue(Types.BLOB, new SqlLobValue(value, lobHandler));
    }

    @Override
    public String getEmbeddedValue(byte[] value) {
        if(value == null) {
            return null;
        }

        StringBuilder buff = new StringBuilder();
        for(byte b : value) {
            buff.append(b);
        }
        return buff.toString();
    }
}

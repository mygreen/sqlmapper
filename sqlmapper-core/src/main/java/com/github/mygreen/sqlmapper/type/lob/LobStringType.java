package com.github.mygreen.sqlmapper.type.lob;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.LobHandler;

import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LobStringType implements ValueType<String> {

    private final LobHandler lobHandler;

    @Override
    public String getValue(ResultSet rs, int columnIndex) throws SQLException {
        return lobHandler.getClobAsString(rs, columnIndex);
    }

    @Override
    public Object getSqlParameterValue(String value) {
        return new SqlParameterValue(Types.CLOB, new SqlLobValue(value, lobHandler));
    }
}

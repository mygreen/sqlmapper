package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.annotation.Temporal.TemporalType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SqlTimestampType implements SqlTemporalType<Timestamp> {

    @Override
    public TemporalType getTemporalType() {
        return TemporalType.TIMESTAMP;
    }

    @Override
    public Timestamp getValue(ResultSet rs, int columnIndex) throws SQLException {

        Timestamp value = rs.getTimestamp(columnIndex);
        if(rs.wasNull()) {
            return null;
        }

        return value;
    }

    @Override
    public void bindValue(Timestamp value, MapSqlParameterSource paramSource, String paramName) {

        paramSource.addValue(paramName, value);
    }
}

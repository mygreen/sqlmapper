package com.github.mygreen.sqlmapper.core.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.RequiredArgsConstructor;

/**
 * 1列しかないResultSetをマッピングします。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class SingleColumnRowMapper<T> implements RowMapper<T> {

    private final ValueType<T> valueType;

    @Override
    public T mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        // 1列名を取得する。
        return valueType.getValue(rs, 1);
    }
}

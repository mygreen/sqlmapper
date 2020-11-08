package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.github.mygreen.sqlmapper.core.type.ValueType;

public class LocalTimeType implements ValueType<LocalTime> {

    private final DateTimeFormatter formatter;

    public LocalTimeType() {
        this("uuuu-MM-dd HH:mm:ss");
    }

    /**
     * フォーマットパターンを指定してインスタンスを作成します。
     * @param pattern 文字列に変換するときのフォーマットパターン
     */
    public LocalTimeType(final String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public LocalTime getValue(ResultSet rs, int columnIndex) throws SQLException {

        Time value = rs.getTime(columnIndex);
        if(value == null) {
            return null;
        }

        return value.toLocalTime();
    }

    @Override
    public Object getSqlParameterValue(LocalTime value) {
        Time sqlValue = (value != null ? Time.valueOf(value) : null);
        return sqlValue;
    }

    @Override
    public String getEmbeddedValue(LocalTime value) {
        return value != null ? formatter.format(value) : null;
    }
}
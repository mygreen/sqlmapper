package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.github.mygreen.sqlmapper.core.type.ValueType;

public class LocalDateTimeType implements ValueType<LocalDateTime> {

    private final DateTimeFormatter formatter;

    public LocalDateTimeType() {
        this("uuuu-MM-dd HH:mm:ss");
    }

    /**
     * フォーマットパターンを指定してインスタンスを作成します。
     * @param pattern 文字列に変換するときのフォーマットパターン
     */
    public LocalDateTimeType(final String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public LocalDateTime getValue(ResultSet rs, int columnIndex) throws SQLException {

        Timestamp value = rs.getTimestamp(columnIndex);
        if(value == null) {
            return null;
        }

        return value.toLocalDateTime();
    }

    @Override
    public Object getSqlParameterValue(LocalDateTime value) {
        return value != null ? Timestamp.valueOf(value) : null;
    }

    @Override
    public String getEmbeddedValue(LocalDateTime value) {
        return value != null ? formatter.format(value) : null;
    }
}

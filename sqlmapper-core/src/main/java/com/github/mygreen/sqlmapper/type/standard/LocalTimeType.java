package com.github.mygreen.sqlmapper.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.type.ValueType;

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
    public void bindValue(LocalTime value, MapSqlParameterSource paramSource, String paramName) {
        Time sqlValue = (value != null ? Time.valueOf(value) : null);
        paramSource.addValue(paramName, sqlValue);
    }

    @Override
    public String getAsText(LocalTime value) {
        return formatter.format(value);
    }
}

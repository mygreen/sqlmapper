package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.github.mygreen.sqlmapper.core.type.ValueType;

public class LocalDateType implements ValueType<LocalDate> {

    private final DateTimeFormatter formatter;

    public LocalDateType() {
        this("uuuu-MM-dd");
    }

    /**
     * フォーマットパターンを指定してインスタンスを作成します。
     * @param pattern 文字列に変換するときのフォーマットパターン
     */
    public LocalDateType(final String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public LocalDate getValue(ResultSet rs, int columnIndex) throws SQLException {

        Date value = rs.getDate(columnIndex);
        if(value == null) {
            return null;
        }

        return value.toLocalDate();
    }

    @Override
    public Object getSqlParameterValue(LocalDate value) {
        return value != null ? Date.valueOf(value) : null;
    }

    @Override
    public String getEmbeddedValue(LocalDate value) {
        return value != null ? formatter.format(value) : null;
    }
}

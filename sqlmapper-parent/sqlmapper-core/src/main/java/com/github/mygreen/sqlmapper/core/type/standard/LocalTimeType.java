package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.Getter;

/**
 * {@link LocalTime} 型のマッピングを処理します。
 * <p>JDBCの型としては、{@link Time} 型として処理を行います。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class LocalTimeType implements ValueType<LocalTime> {

    /**
     * SQLに直接埋め込む時にフォーマットする書式
     */
    @Getter
    private final DateTimeFormatter formatter;

    /**
     * インスタンスを作成します。
     * <p>SQLに直接埋め込む時にフォーマットする書式は、{@literal HH:mm:ss} が指定されます。
     */
    public LocalTimeType() {
        this("HH:mm:ss");
    }

    /**
     * 書式を指定してインスタンスを作成します。
     * @param pattern SQLに直接埋め込む時にフォーマットする書式。
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

    /**
     * {@inheritDoc}
     *
     * @return {@link Time} に変換した値を返します。
     */
    @Override
    public Object getSqlParameterValue(LocalTime value) {
        Time sqlValue = (value != null ? Time.valueOf(value) : null);
        return sqlValue;
    }

    /**
     * {@inheritDoc}
     *
     * 引数が{@literal null}の場合は{@literal null}を返します。
     * @return コンストラクタで指定した書式でフォーマットした値を返します。
     */
    @Override
    public String getEmbeddedValue(LocalTime value) {
        return value != null ? formatter.format(value) : null;
    }
}

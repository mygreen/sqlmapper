package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.Getter;


/**
 * {@link LocalDateTime} 型のマッピングを処理します。
 * <p>JDBCの型としては、{@link Timestamp} 型として処理を行います。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class LocalDateTimeType implements ValueType<LocalDateTime> {

    /**
     * SQLに直接埋め込む時にフォーマットする書式
     */
    @Getter
    private final DateTimeFormatter formatter;

    /**
     * インスタンスを作成します。
     * <p>SQLに直接埋め込む時にフォーマットする書式は、{@literal uuuu-MM-dd HH:mm:ss} が指定されます。
     */
    public LocalDateTimeType() {
        this("uuuu-MM-dd HH:mm:ss");
    }

    /**
     * 書式を指定してインスタンスを作成します。
     * @param pattern SQLに直接埋め込む時にフォーマットする書式。
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

    /**
     * {@inheritDoc}
     *
     * @return {@link Timestamp} に変換した値を返します。
     */
    @Override
    public Object getSqlParameterValue(LocalDateTime value) {
        return value != null ? Timestamp.valueOf(value) : null;
    }

    /**
     * {@inheritDoc}
     *
     * 引数が{@literal null}の場合は{@literal null}を返します。
     * @return コンストラクタで指定した書式でフォーマットした値を返します。
     */
    @Override
    public String getEmbeddedValue(LocalDateTime value) {
        return value != null ? formatter.format(value) : null;
    }
}

package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.Getter;

/**
 * {@link LocalDate} 型のマッピングを処理します。
 * <p>JDBCの型としては、{@link Date} 型として処理を行います。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class LocalDateType implements ValueType<LocalDate> {

    /**
     * SQLに直接埋め込む時にフォーマットするフォーマッター
     */
    @Getter
    private final DateTimeFormatter formatter;

    /**
     * インスタンスを作成します。
     * <p>SQLに直接埋め込む時にフォーマットする書式は、{@literal uuuu-MM-dd} が指定されます。
     */
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

    /**
     * 書式を指定してインスタンスを作成します。
     * @param pattern SQLに直接埋め込む時にフォーマットする書式。
     */
    @Override
    public LocalDate getValue(ResultSet rs, int columnIndex) throws SQLException {

        Date value = rs.getDate(columnIndex);
        if(value == null) {
            return null;
        }

        return value.toLocalDate();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link Date} に変換した値を返します。
     */
    @Override
    public Object getSqlParameterValue(LocalDate value) {
        return value != null ? Date.valueOf(value) : null;
    }

    /**
     * {@inheritDoc}
     *
     * 引数が{@literal null}の場合は{@literal null}を返します。
     * @return コンストラクタで指定した書式でフォーマットした値を返します。
     */
    @Override
    public String getEmbeddedValue(LocalDate value) {
        return value != null ? formatter.format(value) : null;
    }
}

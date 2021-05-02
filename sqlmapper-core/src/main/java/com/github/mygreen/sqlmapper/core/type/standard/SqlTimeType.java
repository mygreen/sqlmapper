package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;

import com.github.mygreen.sqlmapper.core.annotation.Temporal.TemporalType;

import lombok.Getter;

/**
 * {@link Time} 型のマッピングを処理します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlTimeType implements SqlTemporalType<Time> {

    /**
     * SQLに直接埋め込む時にフォーマットする書式
     */
    @Getter
    private final String pattern;

    /**
     * インスタンスを作成します。
     * <p>SQLに直接埋め込む時にフォーマットする書式は、{@literal HH:mm:ss} が指定されます。
     */
    public SqlTimeType() {
        this("HH:mm:ss");
    }

    /**
     * 書式を指定してインスタンスを作成します。
     * @param pattern SQLに直接埋め込む時にフォーマットする書式。
     */
    public SqlTimeType(final String pattern) {
        this.pattern = pattern;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link TemporalType#TIME} を返します。
     */
    @Override
    public TemporalType getTemporalType() {
        return TemporalType.TIME;
    }

    @Override
    public Time getValue(ResultSet rs, int columnIndex) throws SQLException {

        Time value = rs.getTime(columnIndex);
        if(rs.wasNull()) {
            return null;
        }

        return value;
    }

    @Override
    public Object getSqlParameterValue(Time value) {
        return value;
    }

    /**
     * {@inheritDoc}
     *
     * 引数が{@literal null}の場合は{@literal null}を返します。
     * @return コンストラクタで指定した書式でフォーマットした値を返します。
     */
    @Override
    public String getEmbeddedValue(Time value) {
        return value != null ? new SimpleDateFormat(pattern).format(value) : null;
    }
}

package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.github.mygreen.sqlmapper.core.annotation.Temporal.TemporalType;

import lombok.Getter;

/**
 * {@link Date} 型のマッピングを処理します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlDateType implements SqlTemporalType<Date> {

    /**
     * SQLに直接埋め込む時にフォーマットする書式
     */
    @Getter
    private final String pattern;

    /**
     * インスタンスを作成します。
     * <p>SQLに直接埋め込む時にフォーマットする書式は、{@literal yyyy-MM-dd} が指定されます。
     */
    public SqlDateType() {
        this("yyyy-MM-dd");
    }

    /**
     * 書式を指定してインスタンスを作成します。
     * @param pattern SQLに直接埋め込む時にフォーマットする書式。
     */
    public SqlDateType(final String pattern) {
        this.pattern = pattern;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link TemporalType#DATE} を返します。
     */
    @Override
    public TemporalType getTemporalType() {
        return TemporalType.DATE;
    }

    @Override
    public Date getValue(ResultSet rs, int columnIndex) throws SQLException {

        Date value = rs.getDate(columnIndex);
        if(rs.wasNull()) {
            return null;
        }

        return value;
    }

    @Override
    public Object getSqlParameterValue(Date value) {
        return value;
    }

    /**
     * {@inheritDoc}
     *
     * 引数が{@literal null}の場合は{@literal null}を返します。
     * @return コンストラクタで指定した書式でフォーマットした値を返します。
     */
    @Override
    public String getEmbeddedValue(Date value) {
        return value != null ? new SimpleDateFormat(pattern).format(value) : null;
    }

}

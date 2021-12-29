package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.mygreen.sqlmapper.core.annotation.Temporal.TemporalType;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;

import lombok.Getter;

/**
 * {@link Timestamp} 型のマッピングを処理します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlTimestampType implements SqlTemporalType<Timestamp> {

    /**
     * SQLに直接埋め込む時にフォーマットする書式
     */
    @Getter
    private final String pattern;

    /**
     * インスタンスを作成します。
     * <p>SQLに直接埋め込む時にフォーマットする書式は、{@literal yyyy-MM-dd HH:mm:ss.SSS} が指定されます。
     */
    public SqlTimestampType() {
        this("yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     * 書式を指定してインスタンスを作成します。
     * @param pattern SQLに直接埋め込む時にフォーマットする書式。
     */
    public SqlTimestampType(final String pattern) {
        this.pattern = pattern;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link TemporalType#TIMESTAMP} を返します。
     */
    @Override
    public TemporalType getTemporalType() {
        return TemporalType.TIMESTAMP;
    }

    @Override
    public Timestamp convertTo(Date utilDate) {
        return new Timestamp(utilDate.getTime());
    }

    @Override
    public Timestamp getValue(ResultSet rs, int columnIndex) throws SQLException {

        Timestamp value = rs.getTimestamp(columnIndex);
        if(rs.wasNull()) {
            return null;
        }

        return value;
    }

    @Override
    public Object getSqlParameterValue(Timestamp value) {
        return value;
    }

    /**
     * {@inheritDoc}
     *
     * 引数が{@literal null}の場合は{@literal null}を返します。
     * @return コンストラクタで指定した書式でフォーマットした値を返します。
     */
    @Override
    public String getEmbeddedValue(Timestamp value) {
        return value != null ? new SimpleDateFormat(pattern).format(value) : null;
    }

    @Override
    public int getSqlType(Dialect dialect) {
        return Types.TIMESTAMP;
    }

}

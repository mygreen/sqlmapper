package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.Getter;

/**
 * {@link Date} 型のマッピングを処理します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class UtilDateType implements ValueType<Date> {

    /**
     * JDBC型と実際にマッピング処理の型です。
     */
    @Getter
    @SuppressWarnings("rawtypes")
    private final SqlTemporalType temporalConverter;

    /**
     * インスタンスを作成します。
     * @param temporalConverter 実際のマッピングを行う各時制の実装処理。
     */
    @SuppressWarnings("rawtypes")
    public UtilDateType(final SqlTemporalType temporalConverter) {
        this.temporalConverter = temporalConverter;
    }

    @Override
    public Date getValue(ResultSet rs, int columnIndex) throws SQLException {
        return (Date)temporalConverter.getValue(rs, columnIndex);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getSqlParameterValue(Date value) {
        if(value == null) {
            return null;
        }
        return  temporalConverter.getSqlParameterValue(temporalConverter.convertTo(value));
    }

    /**
     * {@inheritDoc}
     *
     * 引数が{@literal null}の場合は{@literal null}を返します。
     * @return コンストラクタで指定した書式でフォーマットした値を返します。
     */
    @SuppressWarnings("unchecked")
    @Override
    public String getEmbeddedValue(Date value) {
        if(value == null) {
            return null;
        }
        return temporalConverter.getEmbeddedValue(temporalConverter.convertTo(value));
    }

    @Override
    public int getSqlType(Dialect dialect) {
        return temporalConverter.getSqlType(dialect);
    }
}

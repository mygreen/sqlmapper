package com.github.mygreen.sqlmapper.core.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.SqlParameterValue;

import com.github.mygreen.splate.type.SqlTemplateValueType;
import com.github.mygreen.splate.type.SqlTypeConversionException;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;

/**
 * SQL(JDBC)とマッピング先の型を表すインタフェースです。
 *
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 * @param <T> マッピング先の型
 */
public interface ValueType<T> extends SqlTemplateValueType<T> {

    /**
     * {@link Types} に基づくSQLタイプを取得します。
     *
     * @since 0.3
     * @param dialect DBの方言
     * @return  {@link Types} に基づくSQLタイプ。
     */
    int getSqlType(Dialect dialect);

    /**
     * カラムの値を返します。
     *
     * @param rs 結果セット
     * @param columnIndex カラムの位置
     * @return カラムの値
     * @throws SQLException 結果セットから値をと出すときにスローされます。
     * @throws SqlValueConversionException SQLの値の変換に失敗したときにストローされます。
     */
    T getValue(ResultSet rs, int columnIndex) throws SQLException, SqlValueConversionException;

    /**
     * SQLのパラメータ変数として値を取得します。
     * <p>JDBCが対応していないタイプの場合は、対応している値に変換します。</p>
     * <p>{@link SqlParameterValue} として返すことで、特殊な値を対応することができます。</p>
     *
     * @param value 変換する値
     * @return SQLのパラメータ変数。
     * @throws SqlParameterBindException SQL変数の値へのバインドに失敗した場合にスローされます。
     */
    Object getSqlParameterValue(T value) throws SqlParameterBindException;

    /**
     * {@inheritDoc}
     * デフォルト実装として、{@link #getSqlParameterValue(Object)} に委譲されます。
     */
    @Override
    default Object getBindVariableValue(T value) throws SqlTypeConversionException {
        return getSqlParameterValue(value);
    }
}

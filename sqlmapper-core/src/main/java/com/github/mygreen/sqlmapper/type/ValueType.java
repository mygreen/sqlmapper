package com.github.mygreen.sqlmapper.type;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * SQL(JDBC)とマッピング先の型を表すインタフェースです。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> マッピング先の型
 */
public interface ValueType<T> {

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
     * SQL変数に値をバインドします。
     *
     * @param value バインドする値。
     * @param paramSource 変数をバインドするパラメータ情報。
     * @param paramName バインドする変数名。
     * @throws SqlParameterBindException SQL変数の値へのバインドに失敗した場合にスローされます。
     */
    void bindValue(T value, MapSqlParameterSource paramSource, String paramName) throws SqlParameterBindException;

    /**
     * SQLに直接埋め込む値として文字列に変換します。
     *
     * @param value 変換する値。非nullが渡されます。
     * @return 文字列に変換した値
     * @throws SqlParameterBindException SQL変数の値へのバインドに失敗した場合にスローされます。
     */
    default String getAsText(T value) {
        if(value == null) {
            return null;
        }
        return value.toString();
    }

}

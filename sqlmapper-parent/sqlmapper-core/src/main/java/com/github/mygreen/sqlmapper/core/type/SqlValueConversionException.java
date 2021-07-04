package com.github.mygreen.sqlmapper.core.type;

import com.github.mygreen.sqlmapper.core.SqlMapperException;

import lombok.Getter;

/**
 * SQL値の変換に失敗したときにスローされる例外です。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlValueConversionException extends SqlMapperException {

    /**
     * 変換後のクラスタイプ。
     */
    @Getter
    private final Class<?> convertType;

    /**
     * 変換対象の値
     */
    @Getter
    private final Object targetValue;

    /**
     * インスタンスを作成します。
     * @param convertType 変換先の型
     * @param targetValue 変換対象の値
     * @param message エラーメッセージ
     */
    public SqlValueConversionException(Class<?> convertType, Object targetValue, String message) {
        super(message);
        this.convertType = convertType;
        this.targetValue = targetValue;

    }

    /**
     * インスタンスを作成します。
     * @param convertType 変換先の型
     * @param targetValue 変換対象の値
     * @param message エラーメッセージ
     * @param cause 原因となる例外
     */
    public SqlValueConversionException(Class<?> convertType, Object targetValue, String message, Throwable cause) {
        super(message, cause);
        this.convertType = convertType;
        this.targetValue = targetValue;

    }
}

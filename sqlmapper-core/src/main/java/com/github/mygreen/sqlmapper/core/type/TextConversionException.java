package com.github.mygreen.sqlmapper.core.type;

import com.github.mygreen.sqlmapper.core.SqlMapperException;

import lombok.Getter;


/**
 * 値を文字列への変換に失敗したときにスローされます。
 *
 * @author T.TSUCHIE
 *
 */
public class TextConversionException extends SqlMapperException {

    /**
     * 変換対象の値
     */
    @Getter
    private final Object targetValue;

    /**
     * メッセージを指定してインスタンスと作成します。
     * @param targetValue 変換対象の値
     * @param message メッセージ
     */
    public TextConversionException(Object targetValue, String message) {
        super(message);
        this.targetValue = targetValue;
    }

    /**
     * メッセージと例外を指定してインスタンスを作成します。
     * @param targetValue 変換対象の値
     * @param message メッセージ
     * @param cause 原因となる例外
     */
    public TextConversionException(Object targetValue, String message, Throwable cause) {
        super(message, cause);
        this.targetValue = targetValue;
    }
}

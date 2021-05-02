package com.github.mygreen.sqlmapper.core.type;

import com.github.mygreen.sqlmapper.core.SqlMapperException;

import lombok.Getter;


/**
 * 値を文字列への変換に失敗したときにスローされる例外です。
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
     * メッセージを指定してインスタンスを作成します。
     * @param targetValue 変換対象の値
     * @param message メッセージ
     */
    public TextConversionException(Object targetValue, String message) {
        super(message);
        this.targetValue = targetValue;
    }

    /**
     * メッセージと原因となるエラーを指定してインスタンスを作成します。
     * @param targetValue 変換対象の値
     * @param message メッセージ
     * @param cause 原因となるエラー
     */
    public TextConversionException(Object targetValue, String message, Throwable cause) {
        super(message, cause);
        this.targetValue = targetValue;
    }
}

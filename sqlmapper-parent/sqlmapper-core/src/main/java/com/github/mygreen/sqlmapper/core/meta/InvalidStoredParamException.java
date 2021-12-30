package com.github.mygreen.sqlmapper.core.meta;

import com.github.mygreen.sqlmapper.core.SqlMapperException;

import lombok.Getter;

/**
 * ストアドのパラメータ情報が不正な場合にスローされる例外です。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class InvalidStoredParamException extends SqlMapperException {

    /**
     * エラー対象のパラメータクラス
     */
    @Getter
    private final Class<?> paramClass;

    /**
     * インスタンスを作成する
     * @param paramClass エラー対象のパラメータクラス
     * @param message エラーメッセージ
     */
    public InvalidStoredParamException(final Class<?> paramClass, final String message) {
        super(message);
        this.paramClass = paramClass;
    }

    /**
     * インスタンスを作成する
     * @param paramClass エラー対象のパラメータクラス
     * @param message エラーメッセージ
     * @param cause 原因となるエラー
     */
    public InvalidStoredParamException(final Class<?> paramClass, final String message, final Throwable cause) {
        super(message, cause);
        this.paramClass = paramClass;
    }
}

package com.github.mygreen.sqlmapper.core.type;

import com.github.mygreen.sqlmapper.core.SqlMapperException;

/**
 * SQL用変数の値のバインドに失敗した場合にスローされる例外です。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlParameterBindException extends SqlMapperException {

    /**
     * メッセージを指定してインスタンスと作成します。
     * @param message メッセージ
     */
    public SqlParameterBindException(String message) {
        super(message);
    }

    /**
     * メッセージと例外を指定してインスタンスを作成します。
     * @param message メッセージ
     * @param cause 原因となる例外
     */
    public SqlParameterBindException(String message, Throwable cause) {
        super(message, cause);
    }
}

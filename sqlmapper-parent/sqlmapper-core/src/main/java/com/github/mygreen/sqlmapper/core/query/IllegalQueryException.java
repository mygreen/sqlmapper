package com.github.mygreen.sqlmapper.core.query;

import com.github.mygreen.sqlmapper.core.SqlMapperException;

/**
 * 不正なクエリが設定されたときにスローされる例外です。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class IllegalQueryException extends SqlMapperException {

    /**
     * メッセージを指定してインスタンスを作成する。
     * @param message メッセージ。
     */
    public IllegalQueryException(String message) {
        super(message);
    }
}

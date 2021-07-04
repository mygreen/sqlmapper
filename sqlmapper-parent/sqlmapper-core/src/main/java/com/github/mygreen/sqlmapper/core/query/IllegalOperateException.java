package com.github.mygreen.sqlmapper.core.query;

import com.github.mygreen.sqlmapper.core.SqlMapperException;

/**
 * 不正なクエリ操作を実行したときにスローされる例外です。
 *
 * @author T.TSUCHIE
 *
 */
public class IllegalOperateException extends SqlMapperException {

    /**
     * メッセージを指定してインスタンスを作成する。
     * @param message メッセージ。
     */
    public IllegalOperateException(String message) {
        super(message);
    }

}

package com.github.mygreen.sqlmapper.query;

import com.github.mygreen.sqlmapper.SqlMapperException;

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

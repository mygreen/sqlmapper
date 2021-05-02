package com.github.mygreen.sqlmapper.core;

/**
 * SqlMapperのルート例外。
 * <p>Spring JDBCの例外はこのクラスには含まれません。</p>
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlMapperException extends RuntimeException {

    /**
     * メッセージと原因となるエラーを指定するコンストラクタです。
     * @param message エラーメッセージ
     * @param cause 原因となるエラー
     */
    public SqlMapperException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * メッセージを指定するコンストラクタです。
     * @param message エラーメッセージ
     */
    public SqlMapperException(String message) {
        super(message);
    }

    /**
     * 原因となるエラーを指定するコンストラクタです。
     * @param cause 原因となるエラー
     */
    public SqlMapperException(Throwable cause) {
        super(cause);
    }

}

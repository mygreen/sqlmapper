package com.github.mygreen.sqlmapper;

/**
 * SqlMapperのルート例外。
 * <p>Spring JDBCの例外はこのクラスには含まない</p>
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlMapperException extends RuntimeException {

    public SqlMapperException(String message) {
        super(message);
    }

    public SqlMapperException(String message, Throwable cause) {
        super(message, cause);
    }

}

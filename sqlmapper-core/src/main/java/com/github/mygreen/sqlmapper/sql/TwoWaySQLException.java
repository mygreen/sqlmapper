package com.github.mygreen.sqlmapper.sql;

import com.github.mygreen.sqlmapper.SqlMapperException;


/**
 * 2Way-SQLのパース時の例外。
 *
 * @author T.TSUCHIE
 *
 */
public class TwoWaySQLException extends SqlMapperException {

    public TwoWaySQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwoWaySQLException(String message) {
        super(message);
    }

    public TwoWaySQLException(Throwable cause) {
        super(cause);
    }
}

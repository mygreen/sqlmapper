package com.github.mygreen.sqlmapper.core.query.sql;

/**
 * SQLテンプレートによる件数のカウントを行うクエリです。
 *
 * @author T.TSUCHIE
 *
 */
public interface SqlCount {

    /**
     * カウント用のクエリを実行します。
     * @return カウント結果
     */
    long getCount();
}

package com.github.mygreen.sqlmapper.core.query.sql;


public interface SqlCount {

    /**
     * カウント用のクエリを実行します。
     * @return カウント結果
     */
    long getCount();
}

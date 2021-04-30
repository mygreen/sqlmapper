package com.github.mygreen.sqlmapper.core.query.sql;


public interface SqlUpdate {

    /**
     * 更新クエリを実行します。
     * @return 更新したレコード件数を返します。
     */
    int execute();
}

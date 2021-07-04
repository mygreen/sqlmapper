package com.github.mygreen.sqlmapper.core.query.sql;

/**
 * SQLテンプレートによる更新（INSERT / UPDATE/ DELETE）を行うクエリです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface SqlUpdate {

    /**
     * 更新クエリを実行します。
     * @return 更新したレコード件数を返します。
     */
    int execute();
}

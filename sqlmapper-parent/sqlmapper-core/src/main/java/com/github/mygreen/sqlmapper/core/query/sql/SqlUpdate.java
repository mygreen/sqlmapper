package com.github.mygreen.sqlmapper.core.query.sql;

/**
 * SQLテンプレートによる更新（INSERT / UPDATE/ DELETE）を行うクエリです。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public interface SqlUpdate {

    /**
     * クエリタイムアウトの秒数を設定します。
     * <p>{@literal -1} を指定するとJDBC ドライバーのデフォルト値を使用します。
     *
     * @since 0.3
     * @param seconds クエリタイムアウトの秒数
     * @return 自身のインスタンス。
     */
    SqlUpdate queryTimeout(int seconds);

    /**
     * 更新クエリを実行します。
     * @return 更新したレコード件数を返します。
     */
    int execute();
}

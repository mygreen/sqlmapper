package com.github.mygreen.sqlmapper.core.query.sql;

/**
 * SQLテンプレートによる件数のカウントを行うクエリです。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public interface SqlCount {

    /**
     * クエリタイムアウトの秒数を設定します。
     * <p>{@literal -1} を指定するとJDBC ドライバーのデフォルト値を使用します。
     *
     * @since 0.3
     * @param seconds クエリタイムアウトの秒数
     * @return 自身のインスタンス。
     */
    SqlCount queryTimeout(int seconds);

    /**
     * カウント用のクエリを実行します。
     * @return カウント結果
     */
    long getCount();
}

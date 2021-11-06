package com.github.mygreen.sqlmapper.core.query.auto;


/**
 * ストアドプロシージャを呼び出すためのSQLを自動生成する処理インタフェースです。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public interface AutoProcedureCall {

    /**
     * クエリタイムアウトの秒数を設定します。
     *
     * @since 0.3
     * @param seconds クエリタイムアウトの秒数
     * @return 自身のインスタンス。
     */
    AutoProcedureCall queryTimeout(int seconds);

    /**
     * ストアドプロシージャを呼び出します。
     */
    void execute();
}

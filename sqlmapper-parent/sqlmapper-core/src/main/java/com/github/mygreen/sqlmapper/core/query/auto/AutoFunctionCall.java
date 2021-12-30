package com.github.mygreen.sqlmapper.core.query.auto;

/**
 * ストアドファンクションを呼び出すためのSQLを自動生成する処理インタフェースです。
 *
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 * @param <T> 戻り値の型
 */
public interface AutoFunctionCall<T> {

    /**
     * クエリタイムアウトの秒数を設定します。
     * <p>{@literal -1} を指定するとJDBC ドライバーのデフォルト値を使用します。
     *
     * @since 0.3
     * @param seconds クエリタイムアウトの秒数
     * @return 自身のインスタンス。
     */
    AutoFunctionCall<T> queryTimeout(int seconds);

    /**
     * ストアドファンクションを呼び出します。
     * @return ファンクションの戻り値
     */
    T execute();
}

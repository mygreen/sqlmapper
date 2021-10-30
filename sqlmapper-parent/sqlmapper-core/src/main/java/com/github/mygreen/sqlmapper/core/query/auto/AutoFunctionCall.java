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
     * ストアドファンクションを呼び出します。
     * @return ファンクションの戻り値
     */
    T execute();
}

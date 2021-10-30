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
     * ストアドプロシージャを呼び出します。
     */
    void execute();
}

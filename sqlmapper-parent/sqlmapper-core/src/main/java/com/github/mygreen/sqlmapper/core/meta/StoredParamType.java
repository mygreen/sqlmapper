package com.github.mygreen.sqlmapper.core.meta;

/**
 * ストアプロシージャのパラメータタイプ
 *
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public enum StoredParamType {

    /** INパラメータ */
    IN,
    /** OUTパラメータ */
    OUT,
    /** IN-OUTパラメータ */
    INOUT,
    /** 結果セット */
    RESULT_SET
}

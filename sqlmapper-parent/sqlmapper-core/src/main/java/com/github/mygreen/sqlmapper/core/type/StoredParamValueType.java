package com.github.mygreen.sqlmapper.core.type;

import java.sql.Types;

/**
 * ストアドプロシージャ／ファンクションのパラメータの型を表します。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public interface StoredParamValueType {

    /**
     * {@link Types} に基づくSQLタイプを取得します。
     * @return  {@link Types} に基づくSQLタイプ。
     */
    int getSqlType();

}

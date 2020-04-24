package com.github.mygreen.sqlmapper.where;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * {@link WhereVisitor} の引数に関する情報。
 * <p>他の情報を引き継ぐ際に使用する。</p>
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class WhereVisitorParamContext {

    /**
     * SQLに埋め込むパラメータのマップ。
     */
    @Getter
    private final MapSqlParameterSource paramSource;

    /**
     * 引数の現在のインデックス
     */
    @Getter
    @Setter
    private int argIndex = 0;

    /**
     * 次の引数のインデックスを取得する
     * @return 引数のインデックス
     */
    public int nextArgIndex() {
        return argIndex++;
    }
}

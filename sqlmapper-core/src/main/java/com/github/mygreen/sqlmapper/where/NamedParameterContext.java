package com.github.mygreen.sqlmapper.where;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 名前付きのSQLのプレースホルダーを組み立てるときのヘルパークラス。
 * <p>最終的に{@link MapSqlParameterSource}を作成します。</p>
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class NamedParameterContext {

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

    /**
     * 変数名を払い出す。
     * @return 変数名
     */
    public String createArgName() {
        return "_arg" + nextArgIndex();
    }

    /**
     * 指定した件数の変数名を払い出す。
     * @param count 払い出す件数。
     * @return 変数名
     */
    public String[] createArgNames(int count) {

        String[] names = new String[count];
        for(int i=0; i < count; i++) {
            names[i] = createArgName();
        }
        return names;
    }
}

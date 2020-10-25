package com.github.mygreen.sqlmapper.query;

import lombok.Getter;
import lombok.Setter;

/**
 * 問い合わせ結果を反復するコンテキストです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class IterationContext {

    /**
     * 反復を終了する場合は{@code true}を設定します。
     */
    @Getter
    @Setter
    private boolean exit;

    /**
     * 現在の行番号を示します。
     */
    @Getter
    @Setter
    private int rowNum;


}

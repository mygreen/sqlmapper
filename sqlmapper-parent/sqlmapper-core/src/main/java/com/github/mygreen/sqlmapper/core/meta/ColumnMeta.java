package com.github.mygreen.sqlmapper.core.meta;

import lombok.Getter;
import lombok.Setter;

/**
 * カラムのメタ情報です。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ColumnMeta {

    /**
     * カラム名。
     */
    @Getter
    @Setter
    private String name;

    /**
     * 更新可能かどうか。
     */
    @Getter
    @Setter
    private boolean updatable = true;

}

package com.github.mygreen.sqlmapper.core.meta;

import com.github.mygreen.sqlmapper.core.util.NameUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * テーブルのメタ情報です。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class TableMeta {

    /**
     * テーブル名
     */
    @Getter
    @Setter
    private String name;

    /**
     * スキーマ。
     */
    @Getter
    @Setter
    private String schema;

    /**
     * カタログ。
     */
    @Getter
    @Setter
    private String catalog;

    /**
     * カタログやスキーマを含んだ完全な名前を返します。
     * @return カタログやスキーマを含んだ完全な名前
     */
    public String getFullName() {

        return NameUtils.tableFullName(name, catalog, schema);
    }
}

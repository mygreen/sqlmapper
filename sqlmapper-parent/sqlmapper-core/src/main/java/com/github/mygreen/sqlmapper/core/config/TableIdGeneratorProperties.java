package com.github.mygreen.sqlmapper.core.config;

import lombok.Data;

/**
 * テーブルによるIDの自動採番の設定
 *
 *
 * @author T.TSUCHIE
 *
 */
@Data
public class TableIdGeneratorProperties {

    /**
     * 生成されたID値を格納するテーブルの名前。
     */
    private String table;

    /**
     * テーブルの含まれるカタログ名。
     */
    private String catalog;

    /**
     * テーブルの含まれるカタログ名。
     */
    private String schema;

    /**
     * テーブル内のシーケンス名を保持する主キーのカラムの名前。
     */
    private String pkColumn;

    /**
     * 生成された最新の値を格納するカラムの名前。
     */
    private String valueColumn;

    /**
     * 一度にインクリメントする量。
     */
    private long allocationSize;

    /**
     * 値の初期値
     */
    private long initialValue;

}

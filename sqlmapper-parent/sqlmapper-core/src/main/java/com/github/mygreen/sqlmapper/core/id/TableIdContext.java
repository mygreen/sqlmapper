package com.github.mygreen.sqlmapper.core.id;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.Id;

import lombok.Data;

/**
 * テーブルを用いたIDを生成するための情報を保持します。
 * <p>このアノテーションは {@link Id}、{@link GeneratedValue} と併わせて使用しなければいけません。</p>
 *
 * @author T.TSUCHIE
 *
 */
@Data
public class TableIdContext {

    /**
     * 生成されたID値を格納するテーブルの名前。
     */
    private String table;

    /**
     * (オプション) テーブルの含まれるカタログ名。
     */
    private String catalog;

    /**
     * (オプション) テーブルの含まれるスキーマ名。
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
     * (オプション) 生成された最後の値を格納するカラムを初期化するために使用される初期値。
     */
    private long initialValue;

    /**
     * (オプション) ジェネレーターが生成した値からID番号を割り当てるときにインクリメントする量。
     */
    private long allocationSize;
}

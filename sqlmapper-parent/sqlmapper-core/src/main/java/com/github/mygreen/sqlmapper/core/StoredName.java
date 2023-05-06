package com.github.mygreen.sqlmapper.core;

import com.github.mygreen.sqlmapper.core.util.NameUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ストアドプロシージャ／ストアドファンクションの名称を指定するためのクラス。
 * <p>スキーマ／カタログを指定する際に利用します。
 *
 * @version 0.4
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class StoredName {

    /**
     * スキーマ名
     */
    @Getter
    private String schema;

    /**
     * カタログ名
     */
    @Getter
    private String catalog;

    /**
     * ストアドプロシージャ／ストアドファンクションの名称
     */
    @Getter
    private final String name;

    /**
     * スキーマ名を設定します。
     * @param schema スキーマ名
     * @return 自身のインスタンス
     */
    public StoredName withSchema(String schema) {
        this.schema = schema;
        return this;
    }

    /**
     * カタログ名を設定します。
     * @param catalog カタログ名
     * @return 自身のインスタンス
     */
    public StoredName withCatalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    /**
     * スキーマ名／カタログ名を考慮したフルネームを取得します。
     * @since 0.4
     * @return フルネームを取得します。
     */
    public String toFullName() {
        return NameUtils.tableFullName(name, catalog, schema);
    }

}

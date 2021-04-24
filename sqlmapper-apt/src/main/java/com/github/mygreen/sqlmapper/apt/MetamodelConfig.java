package com.github.mygreen.sqlmapper.apt;

import java.util.Map;

import lombok.RequiredArgsConstructor;

/**
 * メタモデルの生成オプション。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class MetamodelConfig {

    /**
     * 生成オプションのキー - エンティティのメタモデルクラスの接頭語
     */
    public static final String KEY_PREFIX = "sqlmapper.prefix";

    /**
     * 生成オプションのキー - エンティティのメタモデルクラスの接尾語
     */
    public static final String KEY_SUFFIX = "sqlmapper.suffix";

    /**
     * 生成オプションのキー - 生成ソースのインデント
     */
    public static final String KEY_INDENT = "sqlmapper.indent";

    /**
     * APTの設定オプション
     */
    private final Map<String, String> options;

    /**
     * 生成するエンティティのメタモデルクラスの接尾語
     * @return 初期値 {@literal M}。
     */
    public String getPrefix() {
        return options.getOrDefault(KEY_PREFIX, "M");
    }

    /**
     * 生成するエンティティのメタモデルクラスの接尾語
     * @return 初期値は空文字({@literal ""})。
     */
    public String getSuffix() {
        return options.getOrDefault(KEY_SUFFIX, "");
    }

    /**
     * 生成するソースのインデント
     * @return 初期値は半角スペース4つ({@literal    }
     */
    public String getIndent() {
        return options.getOrDefault(KEY_INDENT, "    ");
    }

}

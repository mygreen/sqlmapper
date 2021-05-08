package com.github.mygreen.sqlmapper.core.config;

import lombok.Data;

/**
 * SQLテンプレートによる設定
 *
 *
 * @author T.TSUCHIE
 *
 */
@Data
public class SqlTemplateProperties {

    /**
     * SQLテンプレートのパース結果をキャッシュするかどうか。
     */
    private boolean cacheMode;

    /**
     * SQLテンプレートのパース時の文字コード
     */
    private String encoding;


}

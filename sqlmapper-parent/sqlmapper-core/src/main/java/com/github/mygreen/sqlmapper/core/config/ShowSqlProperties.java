package com.github.mygreen.sqlmapper.core.config;

import org.slf4j.event.Level;

import lombok.Data;

/**
 * SQLをログに出力する設定。
 *
 * @since 0.4
 * @author T.TSUCHIE
 *
 */
@Data
public class ShowSqlProperties {

    /** SQLのログ出力機能を有効にするかどうか。 */
    private boolean enabled;

    /** SQL出力時のログレベル */
    private Level logLevel;

    /** SQLのバインド変数に関するSQLの出力設定 */
    private BindParamProperties bindParam;

    /**
     * SQLログ出力のバインドパラメータ設定
     *
     * @since 0.4
     * @author T.TSUCHIE
     *
     */
    @Data
    public static class BindParamProperties {

        /** バインド変数を出力するかどうか */
        private boolean enabled;

        /** バインド変数出力時のログレベル */
        private Level LogLevel;
    }

}

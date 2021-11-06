package com.github.mygreen.sqlmapper.core.config;

import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.Data;

/**
 * {@link JdbcTemplate}による設定。
 * <p>各クエリ実行時に上書きすることもできます。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
@Data
public class JdbcTemplateProperties {

    /**
     * フェッチサイズを設定します。
     * <p>これをデフォルト値よりも高く設定すると、大きな結果セットを処理する際に、メモリ消費を犠牲にして処理速度が向上します。
     * <p>デフォルトは -1 で、JDBC ドライバーのデフォルト設定を使用することを示します（つまり、特定のフェッチサイズ設定をドライバーに渡さないようにします）。
     */
    private int fetchSize;

    /**
     * 最大行数を設定します。
     * <p>JDBCのStatementレベルで、結果セットのオブジェクトが含むことのできる最大行数を制限します。制限値を超えた場合は通知なしの除外されます。
     * <p>デフォルトは -1 で、JDBC ドライバーのデフォルト構成を使用することを示します（つまり、特定の最大行設定をドライバーに渡さないようにします）。
     */
    private int maxRows;

    /**
     * クエリ実行時ののクエリタイムアウトを設定します。
     * <p>デフォルトは -1 で、JDBC ドライバーのデフォルトを使用する（つまり、ドライバーで特定のクエリタイムアウト設定を渡さない）ことを示します。
     */
    private int queryTimeout;

    /**
     * SQLの警告を無視するかどうかを設定します。
     * <p>デフォルトは "true" で、すべての警告を飲み込んで記録します。
     * <p>このフラグを "false" に切り替えて、代わりに JdbcTemplate が {@link SQLWarningException} をスローするようにします。
     *
     */
    private boolean ignoreWarning;
}

package com.github.mygreen.sqlmapper.core.query;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mygreen.sqlmapper.core.config.JdbcTemplateProperties;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * {@link JdbcTemplate}を組み立てます。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JdbcTemplateBuilder {

    /**
     * データソース
     */
    private final DataSource dataSource;

    /**
     * 初期値のプロパティ
     */
    private final JdbcTemplateProperties init;

    /**
     * フェッチサイズ
     */
    @Accessors(chain = true, fluent = true)
    @Setter
    private Integer fetchSize;

    /**
     * 最大取得行数
     */
    @Accessors(chain = true, fluent = true)
    @Setter
    private Integer maxRows;

    /**
     * クエリのタイムアウト
     */
    @Accessors(chain = true, fluent = true)
    @Setter
    private Integer queryTimeout;

    /**
     * {@link JdbcTemplate}を組み立てるためのビルダのインスタンスを作成します。
     * @param dataSource データソース
     * @param init 初期値設定値。
     * @return ビルダクラス。
     */
    public static JdbcTemplateBuilder create(DataSource dataSource, @NonNull JdbcTemplateProperties init) {
        JdbcTemplateBuilder builder = new JdbcTemplateBuilder(dataSource, init);
        return builder;
    }

    /**
     * 設定値をもとに{@link JdbcTemplate}のインスタンスを組み立てます。
     * @return {@link JdbcTemplate}のインスタンス。
     */
    public JdbcTemplate build() {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        jdbcTemplate.setIgnoreWarnings(init.isIgnoreWarning());

        if(fetchSize != null) {
            jdbcTemplate.setFetchSize(fetchSize);
        } else {
            jdbcTemplate.setFetchSize(init.getFetchSize());
        }

        if(maxRows != null) {
            jdbcTemplate.setMaxRows(maxRows);
        } else {
            jdbcTemplate.setMaxRows(init.getMaxRows());
        }

        if(queryTimeout != null) {
            jdbcTemplate.setQueryTimeout(queryTimeout);
        } else {
            jdbcTemplate.setQueryTimeout(init.getQueryTimeout());
        }

        return jdbcTemplate;

    }


}

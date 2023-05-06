package com.github.mygreen.sqlmapper.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.github.mygreen.sqlmapper.core.config.JdbcTemplateProperties;
import com.github.mygreen.sqlmapper.core.config.ShowSqlProperties;
import com.github.mygreen.sqlmapper.core.config.SqlTemplateProperties;
import com.github.mygreen.sqlmapper.core.config.TableIdGeneratorProperties;

import lombok.Data;

/**
 * SqlMapperの環境設定
 *
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
@Data
@ConfigurationProperties(SqlMapperProperties.PREFIX)
public class SqlMapperProperties {

    /**
     * プロパティファイルの接頭語
     */
    public static final String PREFIX = "sqlmapper";

    /**
     * {@literal JdbcTemplate}の初期設定値
     */
    private JdbcTemplateProperties jdbcTemplate;

    /**
     * テーブルによるIDの自動採番の設定値
     */
    private TableIdGeneratorProperties tableIdGenerator;

    /**
     * SQLテンプレートの設定値
     */
    private SqlTemplateProperties sqlTemplate;

    /**
     * SQLのログ出力の設定値
     */
    private ShowSqlProperties showSql;

}

package com.github.mygreen.sqlmapper.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.github.mygreen.sqlmapper.core.config.SqlTemplateProperties;
import com.github.mygreen.sqlmapper.core.config.TableIdGeneratorProperties;

import lombok.Data;

/**
 * SqlMapperの環境設定
 *
 *
 * @author T.TSUCHIE
 *
 */
@Data
//@PropertySource("classpath:/com/github/mygreen/sqlmapper/core/sqlmapper.properties")
@ConfigurationProperties(SqlMapperProperties.PREFIX)
public class SqlMapperProperties {

    /**
     * プロパティファイルの接頭語
     */
    public static final String PREFIX = "sqlmapper";

    /**
     * テーブルによるIDの自動採番の設定値
     */
    private TableIdGeneratorProperties tableIdGenerator;

    /**
     * SQLテンプレートの設定値
     */
    private SqlTemplateProperties sqlTemplate;
}

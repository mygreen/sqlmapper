package com.github.mygreen.sqlmapper.core.test.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.mygreen.sqlmapper.core.config.ShowSqlProperties;
import com.github.mygreen.sqlmapper.core.config.SqlMapperConfigurationSupport;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.dialect.H2Dialect;


/**
 * H2接続用の設定
 *
 *
 * @author T.TSUCHIE
 *
 */
@EnableTransactionManagement
@Configuration
public class H2TestConfig extends SqlMapperConfigurationSupport {

    @Override
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("script/h2/create_schema.sql")
                .build();
    }

    @Override
    public Dialect dialect() {
        return new H2Dialect();
    }

    @Override
    public ShowSqlProperties showSqlProperties() {

        ShowSqlProperties prop = super.showSqlProperties();
        prop.setEnabled(true);
        prop.getBindParam().setEnabled(true);

        return prop;
    }
}

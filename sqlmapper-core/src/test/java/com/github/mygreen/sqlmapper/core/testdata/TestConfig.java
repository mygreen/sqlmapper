package com.github.mygreen.sqlmapper.testdata;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.mygreen.sqlmapper.config.SqlMapperConfigureSupport;
import com.github.mygreen.sqlmapper.dialect.Dialect;
import com.github.mygreen.sqlmapper.dialect.H2Dialect;


/**
 * DB接続しない(NamedParameterJdbcTemplateがnull)ときの設定
 *
 *
 * @author T.TSUCHIE
 *
 */
@Configuration
public class TestConfig extends SqlMapperConfigureSupport {

    @Override
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("script/test_schema.sql")
                .addScripts("script/test_data_customer.sql")
                .build();
    }

    @Override
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Override
    public Dialect dialect() {
        return new H2Dialect();
    }



}

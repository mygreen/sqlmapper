package com.github.mygreen.sqlmapper.core.test.config;

import java.sql.Driver;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.mygreen.sqlmapper.core.config.SqlMapperConfigurationSupport;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.dialect.PostgresDialect;

/**
 * PostgreSQL用のテスト設定
 *
 *
 * @author T.TSUCHIE
 *
 */
@EnableTransactionManagement
@Configuration
public class PgsqlTestConfig extends SqlMapperConfigurationSupport {

    @SuppressWarnings("unchecked")
    @Override
    public DataSource dataSource() {
        try {
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            dataSource.setDriverClass((Class<? extends Driver>) Class.forName("org.postgresql.Driver"));
            dataSource.setUrl("jdbc:postgresql://localhost:5432/unit_test_sqlmapper?escapeSyntaxCallMode=callIfNoReturn");
//            dataSource.setUrl("jdbc:postgresql://localhost:5432/unit_test_sqlmapper");
            dataSource.setUsername("sqlmapper");
            dataSource.setPassword("sqlmapper");
            return dataSource;

        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Dialect dialect() {
        return new PostgresDialect();
    }
}

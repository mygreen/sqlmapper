package com.github.mygreen.sqlmapper.core.test.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.config.SqlMapperConfigurationSupport;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.dialect.DialectBase;

/**
 * DB接続しないときのJavaConfig。
 * <p>{@link Dialect}は何もしない。
 * <p>{@link DataSource} は、設定なしの {@link SimpleDriverDataSource}。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Configuration
public class NoDbTestConfig extends SqlMapperConfigurationSupport {

    @Override
    public Dialect dialect() {
        return new NoDbTestDialect();
    }

    @Override
    public DataSource dataSource() {
        return new SimpleDriverDataSource();
    }

    static class NoDbTestDialect extends DialectBase {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean supportsGenerationType(GenerationType generationType) {
            return true;
        }

        @Override
        public DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource,
                String sequenceName) {
            return null;
        }

        @Override
        public String convertLimitSql(String sql, int offset, int limit) {
           return sql;
        }
    }


}

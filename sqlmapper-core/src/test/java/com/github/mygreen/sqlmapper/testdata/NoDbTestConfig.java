package com.github.mygreen.sqlmapper.testdata;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.config.SqlMapperConfigureSupport;
import com.github.mygreen.sqlmapper.dialect.Dialect;
import com.github.mygreen.sqlmapper.dialect.DialectBase;

/**
 * DB接続しない(NamedParameterJdbcTemplateがnull)ときの設定
 *
 *
 * @author T.TSUCHIE
 *
 */
@Configuration
public class NoDbTestConfig extends SqlMapperConfigureSupport {

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return null;
    }

    @Override
    public Dialect dialect() {
        return new NoDbTestDialect();
    }

    @Override
    public DataSource dataSource() {
        return new SimpleDriverDataSource();
    }

    @Override
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    static class NoDbTestDialect extends DialectBase {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean isSupportedGenerationType(GenerationType generationType) {
            return true;
        }

        @Override
        public DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource,
                String sequenceName) {
            return null;
        }
    }


}

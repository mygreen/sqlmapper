package com.github.mygreen.sqlmapper.core.dialect;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;

public class StandardDialect extends DialectBase {

    @Override
    public String getName() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public boolean isSupportedGenerationType(GenerationType generationType) {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }

    @Override
    public DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource,
            String sequenceName) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public String convertLimitSql(String sql, int offset, int limit) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }
}

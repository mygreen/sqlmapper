package com.github.mygreen.sqlmapper;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.mygreen.sqlmapper.dialect.Dialect;
import com.github.mygreen.sqlmapper.naming.NamingRule;

import lombok.Getter;
import lombok.Setter;

/**
 * SqlMapperの設定情報を保持します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlMapperContext {

    @Getter
    @Setter
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Getter
    @Setter
    private NamingRule namingRule;

    @Getter
    @Setter
    private Dialect dialect;

//    @Getter
//    private TransactionTemplate transactionTemplate;
}

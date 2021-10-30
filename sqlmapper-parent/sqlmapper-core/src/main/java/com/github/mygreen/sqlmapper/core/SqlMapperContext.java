package com.github.mygreen.sqlmapper.core;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.splate.SqlTemplateEngine;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.core.meta.StoredParamMetaFactory;
import com.github.mygreen.sqlmapper.core.naming.NamingRule;
import com.github.mygreen.sqlmapper.core.type.ValueTypeRegistry;

import lombok.Getter;
import lombok.Setter;

/**
 * SqlMapperの設定情報を保持します。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public class SqlMapperContext {

    @Getter
    @Setter
    private JdbcTemplate jdbcTemplate;

    @Getter
    @Setter
    private NamingRule namingRule;

    @Getter
    @Setter
    private MessageFormatter messageFormatter;

    @Getter
    @Setter
    private Dialect dialect;

    @Getter
    @Setter
    private EntityMetaFactory entityMetaFactory;

    @Getter
    @Setter
    private StoredParamMetaFactory storedParamMetaFactory;

    /**
     * 主キーの生成時用のトランザクションテンプレート。
     *
     */
    @Getter
    @Setter
    private TransactionTemplate idGeneratorTransactionTemplate;

    /**
     * 各SQL実行時のイベントを配信する機能
     */
    @Getter
    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * SQLテンプレートエンジン
     */
    @Getter
    @Setter
    private SqlTemplateEngine sqlTemplateEngine;

    @Getter
    @Setter
    private ValueTypeRegistry valueTypeRegistry;

}

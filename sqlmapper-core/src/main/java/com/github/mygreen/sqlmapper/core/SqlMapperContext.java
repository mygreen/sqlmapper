package com.github.mygreen.sqlmapper;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.splate.SqlTemplateEngine;
import com.github.mygreen.sqlmapper.dialect.Dialect;
import com.github.mygreen.sqlmapper.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.naming.NamingRule;
import com.github.mygreen.sqlmapper.type.ValueTypeRegistry;

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

    /**
     * 主キーの生成時用のトランザクションテンプレート。
     *
     */
    @Getter
    @Setter
    private TransactionTemplate requiresNewTransactionTemplate;

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

package com.github.mygreen.sqlmapper.core;

import javax.sql.DataSource;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.splate.SqlTemplateEngine;
import com.github.mygreen.sqlmapper.core.config.JdbcTemplateProperties;
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
@Getter
@Setter
public class SqlMapperContext {

    /**
     * テーブル、カラムなどの命名規則。
     */
    private NamingRule namingRule;

    /**
     * エラーメッセージなどのフォーマッター。
     */
    private MessageFormatter messageFormatter;

    /**
     * RDMSごとの方言。
     */
    private Dialect dialect;

    /**
     * エンティティのメタ情報の作成処理。
     */
    private EntityMetaFactory entityMetaFactory;

    /**
     * ストアドプロシージャ／ファンクションのパラメータのメタ情報の作成処理。
     */
    private StoredParamMetaFactory storedParamMetaFactory;

    /**
     * Java ⇔ SQL の相互変換処理を管理。
     */
    private ValueTypeRegistry valueTypeRegistry;

    /**
     * 接続先DBのデータソース。
     */
    private DataSource dataSource;

    /**
     * {@link JdbcTemplate} の初期値。
     */
    private JdbcTemplateProperties jdbcTemplateProperties;

    /**
     * トランザクションマネージャ。
     * <p>主キー生成時のトランザクション設定に使用します。
     */
    private PlatformTransactionManager transactionManager;

    /**
     * 各SQL実行時のイベントを配信する機能
     */
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * SQLテンプレートエンジン
     */
    private SqlTemplateEngine sqlTemplateEngine;

    /**
     * トランザクションの伝搬タイプが {@link TransactionDefinition#PROPAGATION_REQUIRES_NEW} のトランザクションテンプレートを作成します。
     * <p>ID生成用のトランザクションテンプレートとして使用します。
     *
     * @since 0.3
     * @return トランザクションテンプレート。
     */
    public TransactionTemplate txRequiresNew() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return transactionTemplate;
    }

}

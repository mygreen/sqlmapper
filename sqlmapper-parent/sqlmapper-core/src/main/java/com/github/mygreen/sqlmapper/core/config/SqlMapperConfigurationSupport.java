package com.github.mygreen.sqlmapper.core.config;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.messageformatter.MessageInterpolator;
import com.github.mygreen.messageformatter.expression.ExpressionEvaluator;
import com.github.mygreen.messageformatter.expression.SpelExpressionEvaluator;
import com.github.mygreen.splate.SqlTemplateEngine;
import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.audit.AuditingEntityListener;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.core.meta.PropertyMetaFactory;
import com.github.mygreen.sqlmapper.core.naming.DefaultNamingRule;
import com.github.mygreen.sqlmapper.core.naming.NamingRule;
import com.github.mygreen.sqlmapper.core.type.ValueTypeRegistry;

/**
 * SQLMapperのSpringBeanの設定サポート用クラス。
 * 環境ごとに、このクラスを実装してください。
 *
 * @author T.TSUCHIE
 *
 */
@PropertySource("classpath:/com/github/mygreen/sqlmapper/core/sqlmapper.properties")
public abstract class SqlMapperConfigurationSupport implements ApplicationContextAware, ApplicationEventPublisherAware {

    /**
     * Springのアプリケーションコンテキスト
     */
    protected ApplicationContext applicationContext;

    /**
     * イベントを配信する機能
     */
    protected ApplicationEventPublisher applicationEventPublisher;

    /**
     * Springのコンテナの環境設定
     */
    @Autowired
    protected Environment env;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       this.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Bean
    @Description("SQLMapperのクエリ実行用のBean。")
    public SqlMapper sqlMapper() {
        return new SqlMapper(sqlMapperContext());
    }

    @Bean
    @Description("SQLMapperの各設定を保持するBean。")
    public SqlMapperContext sqlMapperContext() {

        final SqlMapperContext context = new SqlMapperContext();
        context.setJdbcTemplate(jdbcTemplate());
        context.setNamingRule(namingRule());
        context.setMessageFormatter(messageFormatter());
        context.setDialect(dialect());
        context.setEntityMetaFactory(entityMetaFactory());
        context.setApplicationEventPublisher(applicationEventPublisher);
        context.setSqlTemplateEngine(sqlTemplateEngine());
        context.setValueTypeRegistry(valueTypeRegistry());
        context.setIdGeneratorTransactionTemplate(idGeneratorTransactionTemplate(transactionManager()));

        return context;

    }

    @Bean
    @Description("SQLテンプレートの設定値")
    public SqlTemplateProperties sqlTemplateProperties() {
        SqlTemplateProperties prop = new SqlTemplateProperties();
        prop.setCacheMode(env.getRequiredProperty("sqlmapper.sql-template.cache-mode", boolean.class));
        prop.setEncoding(env.getProperty("sqlmapper.sql-template.encoding"));

        return prop;
    }

    @Bean
    @Description("テーブルによる自動採番の設定")
    public TableIdGeneratorProperties tableIdGeneratorProperties() {

        TableIdGeneratorProperties prop = new TableIdGeneratorProperties();
        prop.setTable(env.getProperty("sqlmapper.table-id-generator.table"));
        prop.setSchema(env.getProperty("sqlmapper.table-id-generator.schema"));
        prop.setCatalog(env.getProperty("sqlmapper.table-id-generator.catalog"));
        prop.setPkColumn(env.getProperty("sqlmapper.table-id-generator.pk-column"));
        prop.setValueColumn(env.getProperty("sqlmapper.table-id-generator.value-column"));
        prop.setAllocationSize(Long.parseLong(env.getProperty("sqlmapper.table-id-generator.allocation-size")));
        prop.setInitialValue(Long.parseLong(env.getProperty("sqlmapper.table-id-generator.initial-value")));

        return prop;
    }

    @Bean
    @Description("エンティティの対応クラスからメタ情報を作成するBean。")
    public EntityMetaFactory entityMetaFactory() {
        return new EntityMetaFactory();
    }

    @Bean
    @Description("エンティティのプロパティからメタ情報を作成するBean。")
    public PropertyMetaFactory propertyMetaFactory() {
        return new PropertyMetaFactory();
    }

    @Bean
    @Description("テーブルやカラム名の命名規則を定義するBean。")
    public NamingRule namingRule() {
        return new DefaultNamingRule();
    }

    @Bean
    @Description("メッセージをフォーマットするBean。")
    public MessageFormatter messageFormatter() {

        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("com/github/mygreen/sqlmapper/core/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setFallbackToSystemLocale(false);

        // SpELで処理する
        final ExpressionEvaluator expressionEvaluator = new SpelExpressionEvaluator();
        final MessageInterpolator messageInterpolator = new MessageInterpolator(expressionEvaluator);

        return new MessageFormatter(messageSource, messageInterpolator);

    }

    @Bean
    @Description("ValueTypeを管理するBean")
    public ValueTypeRegistry valueTypeRegistry() {
        return new ValueTypeRegistry();

    }

    /**
     * SQLテンプレートエンジンのBean定義。
     * 外部ライブラリ <a href="https://github.com/mygreen/splate/">splate</a> を使用します。
     * <p>キャッシュモードや文字コードの設定はプロパティファイルから取得します。
     *
     * @return SQLテンプレートを処理するエンジン
     */
    @Bean
    @Description("SQLテンプレートを管理するBean")
    public SqlTemplateEngine sqlTemplateEngine() {

        final SqlTemplateEngine templateEngine = new SqlTemplateEngine();
        templateEngine.setCached(sqlTemplateProperties().isCacheMode());
        templateEngine.setEncoding(sqlTemplateProperties().getEncoding());
        templateEngine.setSuffixName(dialect().getName());

        return templateEngine;

    }

    @Bean
    @Description("LOBを処理するBean。")
    public LobHandler lobHandler() {
        return new DefaultLobHandler();
    }

    @Bean
    @Description("SQLクエリを発行するJDBCテンプレート")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    @Description("DB接続するためのデータソース")
    public abstract DataSource dataSource();

    @Bean
    @Description("トランザクションマネージャ")
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    /**
     * ID生成用のトランザクションテンプレートのBean定義。
     * <p>デフォルトでは、トランザクションを独立されるため {@link TransactionDefinition#PROPAGATION_REQUIRES_NEW} を使用します。
     *
     * @param transactionManager トランザクションマネージャ
     * @return トランザクションテンプレート
     */
    @Bean
    @Description("ID生成用のトランザクションテンプレート")
    public TransactionTemplate idGeneratorTransactionTemplate(PlatformTransactionManager transactionManager) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return transactionTemplate;
    }

    @Bean
    @Description("SQLの方言を表現するBean。")
    public abstract Dialect dialect();

    @Bean
    @Description("クエリ実行時のイベントを処理するBean。")
    public AuditingEntityListener auditingEntityListener() {
        return new AuditingEntityListener();
    }

}

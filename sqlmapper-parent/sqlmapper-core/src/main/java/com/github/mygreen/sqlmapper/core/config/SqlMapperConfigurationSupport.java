package com.github.mygreen.sqlmapper.core.config;

import javax.sql.DataSource;

import org.slf4j.event.Level;
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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.messageformatter.MessageInterpolator;
import com.github.mygreen.messageformatter.expression.ExpressionEvaluator;
import com.github.mygreen.messageformatter.expression.SpelExpressionEvaluator;
import com.github.mygreen.splate.SqlTemplateEngine;
import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.audit.AuditingEntityListener;
import com.github.mygreen.sqlmapper.core.config.ShowSqlProperties.BindParamProperties;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.core.meta.PropertyMetaFactory;
import com.github.mygreen.sqlmapper.core.meta.StoredParamMetaFactory;
import com.github.mygreen.sqlmapper.core.meta.StoredPropertyMetaFactory;
import com.github.mygreen.sqlmapper.core.naming.DefaultNamingRule;
import com.github.mygreen.sqlmapper.core.naming.NamingRule;
import com.github.mygreen.sqlmapper.core.query.SqlLogger;
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
        context.setNamingRule(namingRule());
        context.setMessageFormatter(messageFormatter());
        context.setDialect(dialect());
        context.setEntityMetaFactory(entityMetaFactory());
        context.setStoredParamMetaFactory(storedParamMetaFactory());
        context.setApplicationEventPublisher(applicationEventPublisher);
        context.setSqlTemplateEngine(sqlTemplateEngine());
        context.setValueTypeRegistry(valueTypeRegistry());
        context.setDataSource(dataSource());
        context.setJdbcTemplateProperties(jdbcTemplateProperties());
        context.setTransactionManager(transactionManager());
        context.setSqlLogger(sqlLogger());

        return context;

    }


    @Bean
    @Description("JdbcTemplateの設定値")
    public JdbcTemplateProperties jdbcTemplateProperties() {
        JdbcTemplateProperties prop = new JdbcTemplateProperties();
        prop.setFetchSize(env.getRequiredProperty("sqlmapper.jdbc-template.fetch-size", int.class));
        prop.setMaxRows(env.getRequiredProperty("sqlmapper.jdbc-template.max-rows", int.class));
        prop.setQueryTimeout(env.getRequiredProperty("sqlmapper.jdbc-template.query-timeout", int.class));
        prop.setIgnoreWarning(env.getRequiredProperty("sqlmapper.jdbc-template.ignore-warning", boolean.class));

        return prop;
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
    @Description("SQLのログ出力設定")
    public ShowSqlProperties showSqlProperties() {

        ShowSqlProperties prop = new ShowSqlProperties();
        prop.setEnabled(Boolean.parseBoolean(env.getProperty("sqlmapper.show-sql.enabled")));
        prop.setLogLevel(Level.valueOf(env.getProperty("sqlmapper.show-sql.log-level").toUpperCase()));

        BindParamProperties bindProp = new BindParamProperties();
        bindProp.setEnabled(Boolean.parseBoolean(env.getProperty("sqlmapper.show-sql.bind-param.enabled")));
        bindProp.setLogLevel(Level.valueOf(env.getProperty("sqlmapper.show-sql.bind-param.log-level").toUpperCase()));
        prop.setBindParam(bindProp);

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
    @Description("ストアドのパラメータのメタ情報を作成するBean。")
    public StoredParamMetaFactory storedParamMetaFactory() {
        return new StoredParamMetaFactory();
    }

    @Bean
    @Description("ストアドのパラメータのプロパティかからメタ情報を作成するBean。")
    public StoredPropertyMetaFactory storedPropertyMetaFactory() {
        return new StoredPropertyMetaFactory();
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
    @Description("DB接続するためのデータソース")
    public abstract DataSource dataSource();

    @Bean
    @Description("トランザクションマネージャ")
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    @Description("SQLの方言を表現するBean。")
    public abstract Dialect dialect();

    @Bean
    @Description("クエリ実行時のイベントを処理するBean。")
    public AuditingEntityListener auditingEntityListener() {
        return new AuditingEntityListener();
    }

    @Bean
    @Description("SQLのログ出力処理")
    public SqlLogger sqlLogger() {
        return new SqlLogger(showSqlProperties());
    }

}

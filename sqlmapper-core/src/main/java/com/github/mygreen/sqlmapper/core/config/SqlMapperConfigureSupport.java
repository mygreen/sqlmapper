package com.github.mygreen.sqlmapper.core.config;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

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
import com.github.mygreen.sqlmapper.core.type.standard.BigDecimalType;
import com.github.mygreen.sqlmapper.core.type.standard.BooleanType;
import com.github.mygreen.sqlmapper.core.type.standard.DoubleType;
import com.github.mygreen.sqlmapper.core.type.standard.FloatType;
import com.github.mygreen.sqlmapper.core.type.standard.IntegerType;
import com.github.mygreen.sqlmapper.core.type.standard.LocalDateTimeType;
import com.github.mygreen.sqlmapper.core.type.standard.LocalDateType;
import com.github.mygreen.sqlmapper.core.type.standard.LocalTimeType;
import com.github.mygreen.sqlmapper.core.type.standard.LongType;
import com.github.mygreen.sqlmapper.core.type.standard.ShortType;
import com.github.mygreen.sqlmapper.core.type.standard.SqlDateType;
import com.github.mygreen.sqlmapper.core.type.standard.SqlTimeType;
import com.github.mygreen.sqlmapper.core.type.standard.SqlTimestampType;
import com.github.mygreen.sqlmapper.core.type.standard.StringType;
import com.github.mygreen.sqlmapper.core.type.standard.UUIDType;

/**
 * SQLMapperのSpringBeanの設定サポート用クラス。
 * 環境ごとに、このクラスを実装してください。
 *
 * @author T.TSUCHIE
 *
 */
@PropertySource("classpath:/com/github/mygreen/sqlmapper/core/sqlmapper.properties")
public abstract class SqlMapperConfigureSupport implements ApplicationContextAware, ApplicationEventPublisherAware {

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
        context.setIdGeneratorTransactionTemplate(idGeneratorTransactionTemplate(transactionManager(dataSource())));

        return context;

    }

    @Bean
    @Description("エンティティの対応クラスからメタ情報を作成するBean。")
    public EntityMetaFactory entityMetaFactory() {

        final EntityMetaFactory entityMetaFactory = new EntityMetaFactory();
        entityMetaFactory.setMessageFormatter(messageFormatter());
        entityMetaFactory.setNamingRule(namingRule());
        entityMetaFactory.setPropertyMetaFactory(propertyMetaFactory());

        return entityMetaFactory;

    }

    @Bean
    @Description("エンティティのプロパティからメタ情報を作成するBean。")
    public PropertyMetaFactory propertyMetaFactory() {

        final PropertyMetaFactory propertyMetaFactory = new PropertyMetaFactory();
        propertyMetaFactory.setMessageFormatter(messageFormatter());
        propertyMetaFactory.setNamingRule(namingRule());
        propertyMetaFactory.setValueTypeRegistry(valueTypeRegistry());
        propertyMetaFactory.setDialect(dialect());
        propertyMetaFactory.setDataSource(dataSource());
        propertyMetaFactory.setJdbcTemplate(jdbcTemplate());
        propertyMetaFactory.setEnv(env);

        return propertyMetaFactory;

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
        messageSource.addBasenames("com/github/mygreen/sqlmapper/messages");
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

        final ValueTypeRegistry registry = new ValueTypeRegistry();
        registry.setApplicationContext(applicationContext);
        registry.setMessageFormatter(messageFormatter());
        registry.setLobHandler(lobHandler());

        // 文字列用の設定
        registry.register(String.class, new StringType());

        // ブール値用の設定
        registry.register(Boolean.class, new BooleanType(false));
        registry.register(boolean.class, new BooleanType(true));

        // 数値用の設定
        registry.register(Short.class, new ShortType(false));
        registry.register(short.class, new ShortType(true));
        registry.register(Integer.class, new IntegerType(false));
        registry.register(int.class, new IntegerType(true));
        registry.register(Long.class, new LongType(false));
        registry.register(long.class, new LongType(true));
        registry.register(Float.class, new FloatType(false));
        registry.register(float.class, new FloatType(true));
        registry.register(Double.class, new DoubleType(false));
        registry.register(double.class, new DoubleType(true));
        registry.register(BigDecimal.class, new BigDecimalType());

        // SQLの時制用の設定
        // java.util.Date型は、ValueTypeRegistry 内で別途処理される。
        registry.register(Time.class, new SqlTimeType());
        registry.register(java.sql.Date.class, new SqlDateType());
        registry.register(Timestamp.class, new SqlTimestampType());

        // JSR-310の時勢用の設定
        // タイムゾーンを持たない時制のみサポート
        registry.register(LocalTime.class, new LocalTimeType());
        registry.register(LocalDate.class, new LocalDateType());
        registry.register(LocalDateTime.class, new LocalDateTimeType());

        // その他の型の設定
        registry.register(UUID.class, new UUIDType());

        return registry;
    }

    /**
     * SQLテンプレートエンジンのBean定義。
     * 外部ライブラリ splate({@link https://github.com/mygreen/splate/}) を使用します。
     * <p>キャッシュモードや文字コードの設定はプロパティファイルから取得します。
     *
     * @return SQLテンプレートを処理するエンジン
     */
    @Bean
    @Description("SQLテンプレートを管理するBean")
    public SqlTemplateEngine sqlTemplateEngine() {

        final SqlTemplateEngine templateEngine = new SqlTemplateEngine();
        templateEngine.setCached(env.getRequiredProperty("sqlmapper.sqlTemplate.cacheMode", boolean.class));
        templateEngine.setEncoding(env.getProperty("sqlmapper.sqlTemplate.encoding"));
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
    public abstract PlatformTransactionManager transactionManager(DataSource dataSource);

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

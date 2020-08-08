package com.github.mygreen.sqlmapper.config;

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
import com.github.mygreen.sqlmapper.SqlMapper;
import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.audit.AuditingEntityListener;
import com.github.mygreen.sqlmapper.dialect.Dialect;
import com.github.mygreen.sqlmapper.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.meta.PropertyMetaFactory;
import com.github.mygreen.sqlmapper.naming.DefaultNamingRule;
import com.github.mygreen.sqlmapper.naming.NamingRule;
import com.github.mygreen.sqlmapper.type.ValueTypeRegistry;
import com.github.mygreen.sqlmapper.type.standard.BigDecimalType;
import com.github.mygreen.sqlmapper.type.standard.BooleanType;
import com.github.mygreen.sqlmapper.type.standard.DoubleType;
import com.github.mygreen.sqlmapper.type.standard.FloatType;
import com.github.mygreen.sqlmapper.type.standard.IntegerType;
import com.github.mygreen.sqlmapper.type.standard.LocalDateTimeType;
import com.github.mygreen.sqlmapper.type.standard.LocalDateType;
import com.github.mygreen.sqlmapper.type.standard.LocalTimeType;
import com.github.mygreen.sqlmapper.type.standard.LongType;
import com.github.mygreen.sqlmapper.type.standard.ShortType;
import com.github.mygreen.sqlmapper.type.standard.SqlDateType;
import com.github.mygreen.sqlmapper.type.standard.SqlTimeType;
import com.github.mygreen.sqlmapper.type.standard.SqlTimestampType;
import com.github.mygreen.sqlmapper.type.standard.StringType;
import com.github.mygreen.sqlmapper.type.standard.UUIDType;

/**
 *
 *
 * @author T.TSUCHIE
 *
 */
@PropertySource("classpath:/com/github/mygreen/sqlmapper/sqlmapper.properties")
public abstract class SqlMapperConfigureSupport implements ApplicationContextAware, ApplicationEventPublisherAware {

    private ApplicationContext applicationContext;

    private ApplicationEventPublisher applicationEventPublisher;

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
    public SqlMapper sqlMapper() {
        return new SqlMapper(sqlMapperContext());
    }

    @Bean
    public SqlMapperContext sqlMapperContext() {

        SqlMapperContext context = new SqlMapperContext();
        context.setJdbcTemplate(jdbcTemplate());
        context.setNamingRule(namingRule());
        context.setMessageFormatter(messageFormatter());
        context.setDialect(dialect());
        context.setEntityMetaFactory(entityMetaFactory());
        context.setApplicationEventPublisher(applicationEventPublisher);
        context.setSqlTemplateEngine(sqlTemplateEngine());
        context.setValueTypeRegistry(valueTypeRegistry());

        TransactionTemplate requiresNewTransactionTemplate = new TransactionTemplate(transactionManager(dataSource()));
        requiresNewTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        context.setRequiresNewTransactionTemplate(requiresNewTransactionTemplate);

        return context;

    }

    @Bean
    public EntityMetaFactory entityMetaFactory() {

        EntityMetaFactory entityMetaFactory = new EntityMetaFactory();
        entityMetaFactory.setMessageFormatter(messageFormatter());
        entityMetaFactory.setNamingRule(namingRule());
        entityMetaFactory.setPropertyMetaFactory(propertyMetaFactory());

        return entityMetaFactory;

    }

    @Bean
    public PropertyMetaFactory propertyMetaFactory() {

        PropertyMetaFactory propertyMetaFactory = new PropertyMetaFactory();
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
    @Description("テーブルやカラム名の命名規則")
    public NamingRule namingRule() {
        return new DefaultNamingRule();
    }

    @Bean
    public MessageFormatter messageFormatter() {

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("com/github/mygreen/sqlmapper/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setFallbackToSystemLocale(false);

        ExpressionEvaluator expressionEvaluator = new SpelExpressionEvaluator();
        MessageInterpolator messageInterpolator = new MessageInterpolator(expressionEvaluator);

        return new MessageFormatter(messageSource, messageInterpolator);

    }

    @Bean
    public ValueTypeRegistry valueTypeRegistry() {

        ValueTypeRegistry registry = new ValueTypeRegistry();
        registry.setApplicationContext(applicationContext);
        registry.setMessageFormatter(messageFormatter());
        registry.setLobHandler(lobHandler());

        registry.register(String.class, new StringType());

        registry.register(Boolean.class, new BooleanType(false));
        registry.register(boolean.class, new BooleanType(true));

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

        registry.register(Time.class, new SqlTimeType());
        registry.register(java.sql.Date.class, new SqlDateType());
        registry.register(Timestamp.class, new SqlTimestampType());

        registry.register(LocalTime.class, new LocalTimeType());
        registry.register(LocalDate.class, new LocalDateType());
        registry.register(LocalDateTime.class, new LocalDateTimeType());

        registry.register(UUID.class, new UUIDType());

        return registry;
    }

    @Bean
    public SqlTemplateEngine sqlTemplateEngine() {

        final SqlTemplateEngine templateEngine = new SqlTemplateEngine();
        templateEngine.setCached(env.getRequiredProperty("sqlmapper.sqlTemplate.cacheMode", boolean.class));
        templateEngine.setEncoding(env.getProperty("sqlmapper.sqlTemplate.encoding"));
        templateEngine.setSuffixName(dialect().getName());

        return templateEngine;

    }

    @Bean
    public LobHandler lobHandler() {
        return new DefaultLobHandler();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public AuditingEntityListener auditingEntityListener() {
        return new AuditingEntityListener();
    }

    @Bean
    public abstract Dialect dialect();

    @Bean
    public abstract DataSource dataSource();

    @Bean
    public abstract PlatformTransactionManager transactionManager(DataSource dataSource);


}

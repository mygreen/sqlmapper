package com.github.mygreen.sqlmapper.boot.autoconfigure;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
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
import com.github.mygreen.sqlmapper.core.config.SqlTemplateProperties;
import com.github.mygreen.sqlmapper.core.config.TableIdGeneratorProperties;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.dialect.H2Dialect;
import com.github.mygreen.sqlmapper.core.dialect.StandardDialect;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.core.meta.PropertyMetaFactory;
import com.github.mygreen.sqlmapper.core.naming.DefaultNamingRule;
import com.github.mygreen.sqlmapper.core.naming.NamingRule;
import com.github.mygreen.sqlmapper.core.type.ValueTypeRegistry;

import lombok.extern.slf4j.Slf4j;

/**
 * SqlMapperによるAuto-Configuration設定
 *
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
@Configuration
@ConditionalOnClass({ DataSource.class, JdbcTemplate.class})
@ConditionalOnSingleCandidate(DataSource.class)
@PropertySource("classpath:/com/github/mygreen/sqlmapper/core/sqlmapper.properties")
@EnableConfigurationProperties(SqlMapperProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class SqlMapperAutoConfiguration implements ApplicationContextAware, ApplicationEventPublisherAware {

    /**
     * Springのアプリケーションコンテキスト
     */
    private ApplicationContext applicationContext;

    /**
     * イベントを配信する機能
     */
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private SqlMapperProperties sqlMapperProperties;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       this.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlMapper sqlMapper() {
        return new SqlMapper(sqlMapperContext());
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlMapperContext sqlMapperContext() {

        final SqlMapperContext context = new SqlMapperContext();
        context.setJdbcTemplate(jdbcTemplate);
        context.setNamingRule(namingRule());
        context.setMessageFormatter(messageFormatter());
        context.setDialect(dialect());
        context.setEntityMetaFactory(entityMetaFactory());
        context.setApplicationEventPublisher(applicationEventPublisher);
        context.setSqlTemplateEngine(sqlTemplateEngine());
        context.setValueTypeRegistry(valueTypeRegistry());
        context.setIdGeneratorTransactionTemplate(idGeneratorTransactionTemplate());

        return context;

    }

    @Bean
    @ConditionalOnMissingBean
    public SqlTemplateProperties sqlTemplateProperties() {
        return sqlMapperProperties.getSqlTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public TableIdGeneratorProperties tableIdGeneratorProperties() {
        return sqlMapperProperties.getTableIdGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public EntityMetaFactory entityMetaFactory() {
        return new EntityMetaFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public PropertyMetaFactory propertyMetaFactory() {
        return new PropertyMetaFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public NamingRule namingRule() {
        return new DefaultNamingRule();
    }

    @Bean
    @ConditionalOnMissingBean
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
    @ConditionalOnMissingBean
    public ValueTypeRegistry valueTypeRegistry() {
        return new ValueTypeRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlTemplateEngine sqlTemplateEngine() {

        final SqlTemplateEngine templateEngine = new SqlTemplateEngine();
        templateEngine.setCached(sqlTemplateProperties().isCacheMode());
        templateEngine.setEncoding(sqlTemplateProperties().getEncoding());
        templateEngine.setSuffixName(dialect().getName());

        return templateEngine;

    }

    @Bean
    @ConditionalOnMissingBean
    public LobHandler lobHandler() {
        return new DefaultLobHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public Dialect dialect() {

        String url = dataSourceProperties.getUrl();
        if (url != null) {
            DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(url);
            switch(databaseDriver) {
                case H2:
                    return new H2Dialect();
                case POSTGRESQL:
                case SQLITE:
                default:
                    break;
            }
        }

        if(log.isWarnEnabled()) {
            log.warn("StandardDialect was selected, because not explicit configuration and its is not possible to guess from 'sprint.datasource.url' property.");
        }

        return new StandardDialect();

    }

    @Bean
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionTemplate idGeneratorTransactionTemplate() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager());
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return transactionTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditingEntityListener auditingEntityListener() {
        return new AuditingEntityListener();
    }


}

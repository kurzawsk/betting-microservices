package pl.kk.services.common.config;

import com.atomikos.icatch.jta.J2eeUserTransaction;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.annotation.Resource;
import javax.jms.Session;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import java.util.Map;
import java.util.function.Supplier;

@Configuration
@Profile("!test")
public class JmsJpaConfiguration {

    private static final Map<String, String> JPA_PROPERTIES = ImmutableMap.of(
            "hibernate.transaction.factory_class", "org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory",
            "hibernate.transaction.coordinator_class", "jta",
            "hibernate.transaction.jta.platform", "com.atomikos.icatch.jta.hibernate4.AtomikosPlatform",
            "hibernate.query.plan_cache_max_size", "16",
            "hibernate.query.plan_parameter_metadata_max_size", "16");

    @Resource
    private Environment environment;

    @Bean(name = "transactionManager")
    @Autowired
    @Qualifier("userTransactionManager")
    public JtaTransactionManager jtaTransactionManager(TransactionManager transactionManager) {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        J2eeUserTransaction j2eeUserTransaction = new J2eeUserTransaction();
        try {
            j2eeUserTransaction.setTransactionTimeout(300);
        } catch (SystemException e) {
            e.printStackTrace();
        }

        jtaTransactionManager.setTransactionManager(transactionManager);
        jtaTransactionManager.setUserTransaction(j2eeUserTransaction);
        return jtaTransactionManager;
    }

    @Bean(name = "userTransactionManager", initMethod = "init", destroyMethod = "close")
    public UserTransactionManager userTransactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        return userTransactionManager;
    }

    @Bean(name = "jpaVendorAdapter")
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.POSTGRESQL);
        adapter.setDatabasePlatform(PostgreSQL9Dialect.class.getName());
        return adapter;
    }


    @Bean(name = "entityManagerFactory")
    @Autowired
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(JpaVendorAdapter jpaVendorAdapter,
                                                                           AtomikosDataSourceBean atomikosDataSourceBean,
                                                                           @Qualifier("packageToScanSupplier") Supplier<String> packageToScanSupplier) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setPackagesToScan(packageToScanSupplier.get());
        localContainerEntityManagerFactoryBean.setJtaDataSource(atomikosDataSourceBean);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaPropertyMap(JPA_PROPERTIES);
        return localContainerEntityManagerFactoryBean;
    }

    @Bean(name = "xaDataSource", initMethod = "init", destroyMethod = "close")
    public AtomikosDataSourceBean atomikosDataSourceBean() {
        PGXADataSource pgxaDataSource = new PGXADataSource();

        pgxaDataSource.setUrl(environment.getProperty("spring.datasource.url"));
        pgxaDataSource.setPassword(environment.getProperty("spring.datasource.password"));
        pgxaDataSource.setUser(environment.getProperty("spring.datasource.username"));

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(pgxaDataSource);
        xaDataSource.setUniqueResourceName("dataSource");
        xaDataSource.setMinPoolSize(10);
        xaDataSource.setPoolSize(10);
        xaDataSource.setMaxPoolSize(30);
        xaDataSource.setBorrowConnectionTimeout(60);
        xaDataSource.setReapTimeout(20);
        xaDataSource.setMaxIdleTime(60);
        xaDataSource.setMaintenanceInterval(60);
        return xaDataSource;
    }

    @Bean(initMethod = "init")
    public AtomikosConnectionFactoryBean atomikosConnectionFactoryBean() {
        AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
        atomikosConnectionFactoryBean.setUniqueResourceName("ActiveMQXA");
        ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new ActiveMQXAConnectionFactory();
        activeMQXAConnectionFactory.setBrokerURL(environment.getProperty("jms.broker.url"));
        activeMQXAConnectionFactory.setUserName(environment.getProperty("jms.broker.username"));
        activeMQXAConnectionFactory.setPassword(environment.getProperty("jms.broker.password"));
        atomikosConnectionFactoryBean.setXaConnectionFactory(activeMQXAConnectionFactory);
        return atomikosConnectionFactoryBean;
    }

    @Bean
    @Autowired
    public JmsTemplate jmsTemplate(MessageConverter messageConverter, AtomikosConnectionFactoryBean atomikosConnectionFactoryBean) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(atomikosConnectionFactoryBean);
        jmsTemplate.setReceiveTimeout(20000);
        jmsTemplate.setSessionTransacted(true);
        jmsTemplate.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        jmsTemplate.setMessageConverter(messageConverter);
        return jmsTemplate;
    }

    @Bean
    @Autowired
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper);
        return converter;
    }


}

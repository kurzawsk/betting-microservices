package pl.kk.services.common.config;

import com.google.common.collect.ImmutableMap;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.postgresql.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.function.Supplier;

@Configuration
@Profile("!test")
public class JpaConfiguration {

    private static final Map<String, String> JPA_PROPERTIES = ImmutableMap.of(
            "hibernate.query.plan_cache_max_size", "16",
            "hibernate.query.plan_parameter_metadata_max_size", "16");

    @Resource
    private Environment environment;

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
                                                                           @Qualifier("packageToScanSupplier") Supplier<String> packageToScanSupplier) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setPackagesToScan(packageToScanSupplier.get());
        localContainerEntityManagerFactoryBean.setDataSource(dataSource());
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaPropertyMap(JPA_PROPERTIES);
        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .driverClassName(Driver.class.getName())
                .url(environment.getProperty("spring.datasource.url"))
                .username(environment.getProperty("spring.datasource.username"))
                .password(environment.getProperty("spring.datasource.password"))
                .build();
    }

}
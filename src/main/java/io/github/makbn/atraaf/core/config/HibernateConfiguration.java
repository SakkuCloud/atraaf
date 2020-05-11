package io.github.makbn.atraaf.core.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
public class HibernateConfiguration {

    @Value("${datasource.driver-class-name}")
    String DRIVER;

    @Value("${datasource.url}")
    String HOST;

    @Value("${datasource.username}")
    String USERNAME;

    @Value("${datasource.password}")
    String PASSWORD;

    @Value("${datasource.ddl-auto}")
    String DDL_AUTO;

    @Value("${datasource.dialect}")
    String DIALECT;

    @Value("${datasource.maxpool.size}")
    String POOL_SIZE;

    @Primary
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("io.github.makbn.atraaf.core.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(DRIVER);
        ds.setJdbcUrl(HOST);
        ds.setUsername(USERNAME);
        ds.setPassword(PASSWORD);
        ds.setIdleTimeout(15000);
        ds.setAllowPoolSuspension(false);
        ds.setLeakDetectionThreshold(90000);
        ds.setMinimumIdle(10);
        ds.setPoolName("sakku_db_pool");
        ds.setMaxLifetime(100000);
        ds.setMaximumPoolSize(Integer.parseInt(POOL_SIZE));

        return ds;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager
                = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }

    @Qualifier
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("io.github.makbn.atraaf.core.entity");
        factory.setDataSource(dataSource());
        return factory;
    }

    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(
                "hibernate.hbm2ddl.auto", DDL_AUTO);
        hibernateProperties.setProperty(
                "hibernate.dialect", DIALECT);
        hibernateProperties.setProperty(
                "hibernate.show_sql", "false");
        hibernateProperties.setProperty(
                "hibernate.connection.autocommit", "false");
        hibernateProperties.setProperty(
                "hibernate.format_sql", "false");
        hibernateProperties.setProperty(
                "hibernate.temp.use_jdbc_metadata_default", "false");
        hibernateProperties.setProperty(
                "hibernate.globally_quoted_identifiers", "true");
        hibernateProperties.setProperty(
                "hibernate.hikari.maximumPoolSize", POOL_SIZE);
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.idleTimeout", "15000");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.minimumIdle", "10");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.connectionTimeout", "3000");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.maxLifetime", "100000");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.leakDetectionThreshold", "90000");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.cachePrepStmts", "true");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.prepStmtCacheSize", "250");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.prepStmtCacheSqlLimit", "2048");

        return hibernateProperties;
    }
}

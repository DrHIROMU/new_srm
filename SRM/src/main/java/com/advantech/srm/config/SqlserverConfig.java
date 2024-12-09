package com.advantech.srm.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = {"classpath:persistence.properties", "classpath:persistence-${spring.profiles.active}.properties"}
        , encoding = "utf-8"
        , ignoreResourceNotFound = true)
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.advantech.srm.persistence.repository",
        entityManagerFactoryRef = "sqlserverEntityManagerFactory",
        transactionManagerRef = "sqlserverTransactionManager"
)
public class SqlserverConfig {
    @Primary
    @Bean(name = "sqlserverDataSource")
    @ConfigurationProperties("sqlserver.datasource")
    public DataSource defaultDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "sqlserverEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("sqlserverDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("com.advantech.srm.persistence.model").persistenceUnit("sqlserverDb").build();
    }

    @Primary
    @Bean(name = "sqlserverTransactionManager")
    public PlatformTransactionManager defaultTransactionManager(@Qualifier("sqlserverEntityManagerFactory") EntityManagerFactory sqlserverEntityManagerFactory) {
        return new JpaTransactionManager(sqlserverEntityManagerFactory);
    }
}


//@Configuration
//@EnableJpaRepositories(
//        basePackages = "com.example.primary.repository",
//        entityManagerFactoryRef = "primaryEntityManagerFactory",
//        transactionManagerRef = "primaryTransactionManager"
//)
//public class PrimaryDataSourceConfig {
//
//    @Bean(name = "primaryDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.primary")
//    public DataSource primaryDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "primaryEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
//            @Qualifier("primaryDataSource") DataSource primaryDataSource) {
//
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(primaryDataSource);
//        em.setPackagesToScan("com.example.primary.entity"); // 實體類的包
//        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//        return em;
//    }
//
//    @Bean(name = "primaryTransactionManager")
//    public JpaTransactionManager primaryTransactionManager(
//            @Qualifier("primaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory) {
//        return new JpaTransactionManager(primaryEntityManagerFactory.getObject());
//    }
//}

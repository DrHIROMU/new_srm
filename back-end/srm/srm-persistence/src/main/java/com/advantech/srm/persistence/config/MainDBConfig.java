package com.advantech.srm.persistence.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@ConfigurationPropertiesScan
@EnableJpaRepositories(
        basePackages = "com.advantech.srm.persistence.repository.main",
        entityManagerFactoryRef = "mainEntityManagerFactory",
        transactionManagerRef = "mainTransactionManager"
)
@PropertySource(value = {"classpath:persistence.properties", "classpath:persistence-${spring.profiles.active}.properties"}, encoding = "utf-8", ignoreResourceNotFound = true)
public class MainDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "datasource.main")
    public HikariConfig mainHikariConfig() {
        return new HikariConfig();
    }

    @Primary
    @Bean
    public DataSource mainDataSource(HikariConfig mainHikariConfig) {
        return new HikariDataSource(mainHikariConfig);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean mainEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            DataSource mainDataSource,
            JpaProperties mainJpaProperties
    ) {
        return builder
                .dataSource(mainDataSource)
                .packages("com.advantech.srm.persistence.entity.main")
                .persistenceUnit("main")
                .properties(mainJpaProperties.getProperties())
                .build();
    }

    @Bean
    public PlatformTransactionManager mainTransactionManager(EntityManagerFactory mainEntityManagerFactory) {
        return new JpaTransactionManager(mainEntityManagerFactory);
    }
}

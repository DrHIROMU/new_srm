package com.advantech.srm.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MainDBConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.main")
    public DataSourceProperties mainDBProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource mainDataSource() {
        return mainDBProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public JdbcTemplate mainJdbcTemplate(DataSource mainDataSource) {
        return new JdbcTemplate(mainDataSource);
    }

    @Bean
    public DataSourceTransactionManager mainTxManager(DataSource mainDataSource) {
        return new DataSourceTransactionManager(mainDataSource);
    }
}

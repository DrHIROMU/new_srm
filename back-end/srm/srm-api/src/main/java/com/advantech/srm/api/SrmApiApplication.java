package com.advantech.srm.api;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;

@SpringBootApplication(
        scanBasePackages = {
                "com.advantech.srm.api",
                "com.advantech.srm.persistence"
        }
)
public class SrmApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrmApiApplication.class, args);
    }

    @Autowired
    DataSource mainDataSource;

    @PostConstruct
    public void printHikariStats() {
        if (mainDataSource instanceof com.zaxxer.hikari.HikariDataSource hds) {
            System.out.println(System.getProperty("file.encoding"));
            System.out.println("最大連線數:" + hds.getMaximumPoolSize());
            System.out.println("最小 idle: " + hds.getMinimumIdle());
            System.out.println("JDBC URL: " + hds.getJdbcUrl());
            System.out.println("Driver: " + hds.getDriverClassName());
            System.out.println("PoolName: " + hds.getPoolName());
        }
    }
}

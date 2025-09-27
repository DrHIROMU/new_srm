package com.advantech.srm.auth;

import com.advantech.srm.persistence.config.MainDBConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(MainDBConfig.class)
public class SrmAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrmAuthApplication.class, args);
    }

}

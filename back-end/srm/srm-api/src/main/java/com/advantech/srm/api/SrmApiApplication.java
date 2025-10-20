package com.advantech.srm.api;

import com.advantech.srm.persistence.config.MainDBConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(MainDBConfig.class)
public class SrmApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SrmApiApplication.class, args);
  }
}

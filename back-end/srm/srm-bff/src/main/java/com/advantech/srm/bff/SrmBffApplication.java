package com.advantech.srm.bff;

import com.advantech.srm.bff.config.BffProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BffProperties.class)
public class SrmBffApplication {

  public static void main(String[] args) {
    SpringApplication.run(SrmBffApplication.class, args);
  }
}

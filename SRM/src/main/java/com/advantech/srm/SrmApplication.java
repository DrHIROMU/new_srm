package com.advantech.srm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootApplication
public class SrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(SrmApplication.class, args);
	}

}

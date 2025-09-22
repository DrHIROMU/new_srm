package com.advantech.srm.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.advantech.srm.scheduler",
                "com.advantech.srm.persistence"
        }
)
public class SrmSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SrmSchedulerApplication.class, args);
	}

}

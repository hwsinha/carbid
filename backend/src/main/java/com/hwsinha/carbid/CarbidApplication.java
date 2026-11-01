package com.hwsinha.carbid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CarbidApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarbidApplication.class, args);
	}

}

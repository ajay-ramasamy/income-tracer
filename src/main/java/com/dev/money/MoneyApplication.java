package com.dev.money;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MoneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneyApplication.class, args);
	}

}

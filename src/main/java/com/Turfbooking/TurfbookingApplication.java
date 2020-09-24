package com.Turfbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class TurfbookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurfbookingApplication.class, args);
	}

}

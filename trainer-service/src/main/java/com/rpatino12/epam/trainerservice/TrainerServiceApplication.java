package com.rpatino12.epam.trainerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication
public class TrainerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainerServiceApplication.class, args);
	}

}

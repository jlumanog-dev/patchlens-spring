package com.jlumanog_dev.patchlens_spring_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PatchlensSpringBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PatchlensSpringBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx){
		return args -> System.out.println("hello world");
	}

}

package com.app.foodbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FoodBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodBackendApplication.class, args);
	}

}

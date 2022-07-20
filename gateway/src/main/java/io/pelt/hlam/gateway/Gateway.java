package io.pelt.hlam.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableReactiveFeignClients
public class Gateway {
	public static void main(String[] args) {
		SpringApplication.run(Gateway.class, args);
	}

}

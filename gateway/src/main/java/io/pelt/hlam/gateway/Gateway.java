package io.pelt.hlam.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.security.interfaces.RSAPublicKey;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class Gateway {
	public static void main(String[] args) {
		SpringApplication.run(Gateway.class, args);
	}

	@Autowired
	@Bean
	@Lazy
	RSAPublicKey getRSAPublicKey(AuthService authService){
		return authService.getPublicKey();
	}
}

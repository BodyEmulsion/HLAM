package io.pelt.hlam.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class WeatherConfig {
    public static void main(String[] args) {
        SpringApplication.run(WeatherConfig.class, args);
    }
}
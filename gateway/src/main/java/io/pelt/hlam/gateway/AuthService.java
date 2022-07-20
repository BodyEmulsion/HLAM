package io.pelt.hlam.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Map;

@ReactiveFeignClient("auth-service")
public interface AuthService {
    @GetMapping(path = "/get-public-key")
    Mono<Map<String, BigInteger>> getPublicKey();
}

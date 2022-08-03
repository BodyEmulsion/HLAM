package io.pelt.hlam.gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient("auth-service")
public interface AuthService {
    @GetMapping(path = "/get-public-key")
    Mono<ResponseEntity<byte[]>> getPublicKey();
}

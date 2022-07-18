package io.pelt.hlam.gateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.spec.X509EncodedKeySpec;

@FeignClient("auth-service")
public interface AuthService {
    @GetMapping(path = "/get-public-key")
    ResponseEntity<X509EncodedKeySpec> getPublicKey();
}

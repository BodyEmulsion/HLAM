package io.pelt.hlam.gateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.interfaces.RSAPublicKey;

@FeignClient(name = "auth-service", url = "lb://auth-service")
public interface AuthService {
    @GetMapping(path = "/get-public-key")
    RSAPublicKey getPublicKey();
}

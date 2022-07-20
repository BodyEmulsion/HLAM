package io.pelt.hlam.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Map;


@RestController()
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping(path = "login")
    public ResponseEntity<String> getJWT(@RequestParam("username") String username, @RequestParam("password") String password){
        try {
            return new ResponseEntity<>(authService.getJWT(username, password), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (WrongPasswordException e) {
            return new ResponseEntity<>("Wrong password", HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

    @GetMapping(path = "get-public-key")
    public Mono<Map<String, BigInteger>> getPublicKey(){
        try {
            var spec = this.authService.getPublicKey();
            return Mono.just(Map.of("modulus", spec.getModulus(), "exp", spec.getPublicExponent()));
        } catch (Exception e) {
           return Mono.error(e);
        }
    }
}

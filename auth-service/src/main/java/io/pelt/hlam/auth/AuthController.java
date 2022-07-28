package io.pelt.hlam.auth;

import io.pelt.hlam.auth.exceptions.DatabaseDefaultValueException;
import io.pelt.hlam.auth.exceptions.UserNotFoundException;
import io.pelt.hlam.auth.exceptions.WrongPasswordException;
import io.pelt.hlam.auth.model.RegistrationUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
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
            var headers = new HttpHeaders();
            headers.add("error-message", "User not found");
            return new ResponseEntity<>(headers, HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (WrongPasswordException e) {
            var headers = new HttpHeaders();
            headers.add("error-message", "Wrong password");
            return new ResponseEntity<>(headers, HttpStatus.UNPROCESSABLE_ENTITY);
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

    @PostMapping(path = "guest-login")
    public ResponseEntity<String> getGuestJWT(){
        try {
            return new ResponseEntity<>(this.authService.getGuestJWT(), HttpStatus.OK);
        } catch (DatabaseDefaultValueException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "register")
    public ResponseEntity<String> register(@Valid RegistrationUserDto userDto){
        try {
            return new ResponseEntity<>(authService.register(userDto), HttpStatus.OK);
        } catch (DatabaseDefaultValueException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

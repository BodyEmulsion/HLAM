package io.pelt.hlam.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.interfaces.RSAPublicKey;


@RestController
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

    @GetMapping(path = "validate") //TODO: Move to gateway and add some validation
    public ResponseEntity<String> validateJWT(@RequestHeader("jwt") String jwt){
        var algorithm = Algorithm.RSA512(this.authService.getPublicKey(), null);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            verifier.verify(jwt);
        } catch (JWTVerificationException e) {
            return new ResponseEntity<>("invalid JWT", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "get-public-key")
    public ResponseEntity<RSAPublicKey> getPublicKey(){
        return new ResponseEntity<>(this.authService.getPublicKey(), HttpStatus.OK);
    }
}

package io.pelt.hlam.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;


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

//    @GetMapping(path = "validate") //TODO: Move to gateway and add some validation
//    public ResponseEntity<String> validateJWT(@RequestHeader("jwt") String jwt){
//        var algorithm = Algorithm.RSA512(this.authService.getPublicKey(), null);
//        JWTVerifier verifier = JWT.require(algorithm).build();
//        try {
//            verifier.verify(jwt);
//        } catch (JWTVerificationException e) {
//            return new ResponseEntity<>("invalid JWT", HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @GetMapping(path = "get-public-key")
    public ResponseEntity<X509EncodedKeySpec> getPublicKey(){
        try {
            return new ResponseEntity<>(this.authService.getPublicKey(), HttpStatus.OK);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}

package io.pelt.hlam.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.pelt.hlam.auth.model.Role;
import io.pelt.hlam.auth.model.User;
import io.pelt.hlam.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    AuthService() throws NoSuchAlgorithmException {
        var keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        var keyPair = keyGen.generateKeyPair();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    public String getJWT(String username, String password) throws UserNotFoundException, WrongPasswordException {
        Optional<User> user = this.userRepository.findByUsername(username);
        if (user.isEmpty()){
            throw new UserNotFoundException();
        }
        if (!passwordEncoder.matches(password, user.get().getPassword())){
            throw new WrongPasswordException();
        }
        return generateJWT(user.get());
    }

    public RSAPublicKey getPublicKey(){
        return this.publicKey;
    }

    private String generateJWT(User user) {
        var tokenData = new HashMap<String, String>();
        tokenData.put("username", user.getUsername());
        tokenData.put("roles", user.getRoles().toString());
        tokenData.put("privileges",
                user.getRoles().stream()
                        .flatMap((Role r) -> r.getPrivileges().stream())
                        .distinct()
                        .collect(Collectors.toList()).toString()
        );
        tokenData.put("id", user.getId().toString());
        tokenData.put("create_time", String.valueOf(new Date().getTime()));
        var calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        tokenData.put("expiration_date", String.valueOf(calendar.getTime()));
        Algorithm algorithm = Algorithm.RSA512(null, privateKey);
        return JWT.create()
                .withPayload(tokenData)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }
}

package io.pelt.hlam.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.pelt.hlam.auth.entity.Privilege;
import io.pelt.hlam.auth.entity.Role;
import io.pelt.hlam.auth.entity.User;
import io.pelt.hlam.auth.enums.DefaultRole;
import io.pelt.hlam.auth.exceptions.DatabaseDefaultValueException;
import io.pelt.hlam.auth.exceptions.UserNotFoundException;
import io.pelt.hlam.auth.exceptions.WrongPasswordException;
import io.pelt.hlam.auth.model.RegistrationUserDto;
import io.pelt.hlam.auth.repository.RoleRepository;
import io.pelt.hlam.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
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

    public RSAPublicKeySpec getPublicKey() {
        return new RSAPublicKeySpec(publicKey.getModulus(), publicKey.getPublicExponent());
    }

    private String generateJWT(User user) {
        Map<String, Object> tokenData = getClaimsMap(user);
        var calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        Algorithm algorithm = Algorithm.RSA512(null, privateKey);
        return JWT.create()
                .withPayload(tokenData)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    private Map<String, Object> getClaimsMap(User user) {
        var tokenData = new HashMap<String, Object>();
        tokenData.put("id", user.getId().toString());
        tokenData.put("username", user.getUsername());
        tokenData.put("roles", user.getRoles().stream().map(Role::toString).collect(Collectors.toList()));
        tokenData.put("privileges",
                user.getRoles().stream()
                        .flatMap((Role r) -> r.getPrivileges().stream())
                        .map(Privilege::toString)
                        .distinct()
                        .collect(Collectors.toList())
        );
        return tokenData;
    }

    public String getGuestJWT() throws DatabaseDefaultValueException {
        Optional<Role> role = roleRepository.findById(DefaultRole.GUEST.getId());
        if(role.isEmpty()){
            throw new DatabaseDefaultValueException("DB doesn't contain the User role");
        }
        User guest = User.builder()
                .roles(List.of(role.get()))
                .build();
        guest = this.userRepository.save(guest);
        return generateJWT(guest);
    }

    public String register(RegistrationUserDto userDto) throws DatabaseDefaultValueException {
        Optional<Role> role = roleRepository.findById(DefaultRole.USER.getId());
        if (role.isEmpty()){
            throw new DatabaseDefaultValueException("DB doesn't contain the User role");
        }
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .roles(List.of(role.get()))
                .build();
        user = userRepository.save(user);
        return generateJWT(user);
    }
}

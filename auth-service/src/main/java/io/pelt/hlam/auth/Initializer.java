package io.pelt.hlam.auth;

import io.pelt.hlam.auth.model.Privilege;
import io.pelt.hlam.auth.model.Role;
import io.pelt.hlam.auth.model.User;
import io.pelt.hlam.auth.repository.PrivilegeRepository;
import io.pelt.hlam.auth.repository.RoleRepository;
import io.pelt.hlam.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Initializer {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        var mirror = Privilege.builder()
                .name("mirror")
                .build();
        var actuator = Privilege.builder()
                .name("actuator")
                .build();
        var publicKey = Privilege.builder()
                .name("public-key")
                .build();
        var role = Role.builder()
                .name("admin")
                .build();
        var user = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .build();

        mirror.setRoles(List.of(role));
        actuator.setRoles(List.of(role));
        publicKey.setRoles(List.of(role));
        role.setPrivileges(List.of(mirror, actuator, publicKey));
        user.setRoles(List.of(role));

        privilegeRepository.save(mirror);
        privilegeRepository.save(actuator);
        privilegeRepository.save(publicKey);
        roleRepository.save(role);
        userRepository.save(user);
    }
}

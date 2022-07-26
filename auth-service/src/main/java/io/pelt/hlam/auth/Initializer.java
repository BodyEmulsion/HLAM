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
        var adminRole = Role.builder()
                .name("admin")
                .build();
        var adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .build();
        var guestRole = Role.builder()
                .name("guest")
                .privileges(List.of(mirror))
                .build();
        var userRole = Role.builder()
                .name("user")
                .privileges(List.of(mirror))
                .build();
        mirror.setRoles(List.of(adminRole));
        actuator.setRoles(List.of(adminRole));
        publicKey.setRoles(List.of(adminRole));
        adminRole.setPrivileges(List.of(mirror, actuator, publicKey));
        adminUser.setRoles(List.of(adminRole));

        privilegeRepository.save(mirror);
        privilegeRepository.save(actuator);
        privilegeRepository.save(publicKey);
        roleRepository.save(adminRole);
        roleRepository.save(guestRole);
        roleRepository.save(userRole);
        userRepository.save(adminUser);
    }
}

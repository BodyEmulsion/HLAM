package io.pelt.hlam.auth;

import io.pelt.hlam.auth.entity.Privilege;
import io.pelt.hlam.auth.entity.RegisteredUser;
import io.pelt.hlam.auth.entity.Role;
import io.pelt.hlam.auth.repository.PrivilegeRepository;
import io.pelt.hlam.auth.repository.RegisteredUserRepository;
import io.pelt.hlam.auth.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Initializer {
    @Autowired
    private RegisteredUserRepository registeredUserRepository;
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
        var guestRole = Role.builder()
                .name("guest")
                .build();
        var userRole = Role.builder()
                .name("user")
                .build();
        var adminUser = RegisteredUser.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .build();
        adminRole
                .addPrivilege(mirror)
                .addPrivilege(actuator)
                .addPrivilege(publicKey);
        guestRole.addPrivilege(mirror);
        userRole.addPrivilege(mirror);
        adminUser.addRole(adminRole);

        privilegeRepository.save(mirror);
        privilegeRepository.save(actuator);
        privilegeRepository.save(publicKey);
        roleRepository.save(adminRole);
        roleRepository.save(guestRole);
        roleRepository.save(userRole);
        registeredUserRepository.save(adminUser);
    }
}

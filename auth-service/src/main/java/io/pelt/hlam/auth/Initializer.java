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
        var privilege = Privilege.builder()
                .name("TestPrivilege")
                .build();
        var role = Role.builder()
                .name("TestRole")
                .build();
        var user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .build();

        privilege.setRoles(List.of(role));
        role.setPrivileges(List.of(privilege));
        user.setRoles(List.of(role));

        privilegeRepository.save(privilege);
        roleRepository.save(role);
        userRepository.save(user);
    }
}

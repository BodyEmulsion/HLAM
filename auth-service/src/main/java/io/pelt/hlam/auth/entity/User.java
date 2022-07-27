package io.pelt.hlam.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany()
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public User addRole(Role role){
        if (this.roles == null)
            this.roles = new HashSet<>();
        this.roles.add(role);
        if (role.getUsers() == null)
            role.setUsers(new HashSet<>());
        role.getUsers().add(this);
        return this;
    }

    public Map<String, Object> getClaimsMap() {
        var tokenData = new HashMap<String, Object>();
        tokenData.put("id", this.getId().toString());
        tokenData.put("roles", this.getRoles().stream().map(Role::toString).collect(Collectors.toList()));
        tokenData.put("privileges",
                this.getRoles().stream()
                        .flatMap((Role r) -> r.getPrivileges().stream())
                        .map(Privilege::toString)
                        .distinct()
                        .collect(Collectors.toList())
        );
        return tokenData;
    }
}

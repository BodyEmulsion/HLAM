package io.pelt.hlam.auth.model;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "auth_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String email;
    private String username;
    private String password;
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
}

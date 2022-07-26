package io.pelt.hlam.auth.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;
    @ManyToMany
    @JoinTable(
            name = "roles_privileges",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "privileges_id", referencedColumnName = "id"))
    private Collection<Privilege> privileges;

    @Override
    public String toString(){
        return id.toString() + ":" + name;
    }

    public Role addPrivilege(Privilege privilege){
        if (this.privileges == null)
            this.privileges = new HashSet<>();
        this.privileges.add(privilege);
        if (privilege.getRoles() == null)
            privilege.setRoles(new HashSet<>());
        privilege.getRoles().add(this);
        return this;
    }
}

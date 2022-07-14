package io.pelt.hlam.auth.model;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;

    @Override
    public String toString(){
        return id.toString() + ":" + name;
    }
}

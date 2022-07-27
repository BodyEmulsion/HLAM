package io.pelt.hlam.auth.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Collection;
import java.util.Map;


@Getter
@Setter
@Entity
@NoArgsConstructor
public class RegisteredUser extends User{
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;
    private String password;

    @Builder
    public RegisteredUser(Long id, Collection<Role> roles, String email, String username, String password) {
        super(id, roles);
        this.email = email;
        this.username = username;
        this.password = password;
    }

    @Override
    public Map<String, Object> getClaimsMap() {
        var tokenData = super.getClaimsMap();
        tokenData.put("username", this.getUsername());
        return tokenData;
    }
}

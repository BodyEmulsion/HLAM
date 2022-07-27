package io.pelt.hlam.auth.entity;

import lombok.*;
import org.apache.commons.lang.time.DateUtils;

import javax.persistence.Entity;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Guest extends User {
    private Date expiryDate;

    @Builder
    public Guest(Long id, Collection<Role> roles) {
        super(id, roles);
        this.expiryDate = DateUtils.addDays(new Date(), 30);
    }

    @Override
    public Map<String, Object> getClaimsMap() {
        var tokenData = super.getClaimsMap();
        tokenData.put("expiryDate", this.expiryDate);
        return tokenData;
    }
}

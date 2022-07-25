package io.pelt.hlam.auth.enums;

import lombok.Getter;

@Getter
public enum DefaultRole {
    ADMIN(1L),
    USER(3L),
    GUEST(2L);
    private final Long id;
    DefaultRole(Long id){
        this.id = id;
    }
}
//TODO: fix problem with ID which generates according to the order of saving entities
package com.example.wiki.entity;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ANONYMOUS,
    ADMIN,
    USER;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}

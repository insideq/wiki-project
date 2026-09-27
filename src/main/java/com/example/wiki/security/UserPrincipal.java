package com.example.wiki.security;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.wiki.entity.UserEntity;
import com.example.wiki.entity.UserRole;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Set<? extends GrantedAuthority> roles;
    private final boolean active;
    private final UserEntity user;

    private UserPrincipal(
            Long id,
            String username,
            String password,
            Set<? extends GrantedAuthority> roles,
            boolean active,
            UserEntity user) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.active = active;
        this.user = user;
    }

    public UserPrincipal(UserEntity user) {
        this.id = user.getId();
        this.username = user.getLogin();
        this.password = user.getPassword();
        this.roles = Set.of(user.getRole());
        this.active = true;
        this.user = user;
    }

    public static UserPrincipal anonymous() {
        return new UserPrincipal(-1L, "anonymous", null, Set.of(UserRole.ANONYMOUS), true, null);
    }

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }
}
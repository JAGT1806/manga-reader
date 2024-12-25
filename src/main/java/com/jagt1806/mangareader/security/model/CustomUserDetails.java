package com.jagt1806.mangareader.security.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {
    private final Long id;
    private final Boolean enabled;

    public CustomUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities, boolean enabled) {
        super(username, password, authorities);
        this.id = id;
        this.enabled = enabled;
    }
}

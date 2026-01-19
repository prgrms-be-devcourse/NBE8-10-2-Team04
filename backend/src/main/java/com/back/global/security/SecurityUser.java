package com.back.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class SecurityUser extends User {
    private final Long id;
    private final String email;

    public SecurityUser(Long id,
                        String loginId,
                        String password,
                        String email,
                        Collection<? extends GrantedAuthority> authorities) {
        super(loginId, password == null ? "" : password, authorities);
        this.id = id;
        this.email = email;
    }
}
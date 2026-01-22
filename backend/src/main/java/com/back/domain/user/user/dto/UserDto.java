package com.back.domain.user.user.dto;

import com.back.domain.user.user.entity.User;

public record UserDto(
        long id,
        String loginId,
        String email
) {
    public UserDto(long id, String loginId, String email) {
        this.id = id;
        this.loginId = loginId;
        this.email = email;
    }

    public UserDto(User user) {
        this(
                user.getId(),
                user.getLoginId(),
                user.getEmail()
        );
    }
}
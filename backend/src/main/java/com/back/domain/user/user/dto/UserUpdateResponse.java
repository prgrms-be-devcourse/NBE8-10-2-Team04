package com.back.domain.user.user.dto;

import com.back.domain.user.user.entity.User;

public record UserUpdateResponse(
        String loginId,
        String email
) {
    public UserUpdateResponse(User user) {
        this(
                user.getLoginId(),
                user.getEmail()
        );
    }
}

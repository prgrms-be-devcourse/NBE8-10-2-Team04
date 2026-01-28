package com.back.domain.user.user.dto;

public record UserLoginResponse(
        UserDto userDto,
        String apiKey,
        String accessToken
) {
}

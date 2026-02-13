package com.back.domain.user.user.dto;

import com.back.domain.user.user.entity.User;

public record UserLoginResponse(
        UserDto user,
        String apiKey,
        String accessToken
) {
    /**
     * 로그인 성공 시 User Entity와 토큰들을 Response DTO로 변환
     *
     * @param user 로그인한 사용자
     * @param apiKey API 키
     * @param accessToken 액세스 토큰
     * @return UserLoginResponse DTO
     */
    public static UserLoginResponse of(User user, String apiKey, String accessToken) {
        return new UserLoginResponse(
                UserDto.from(user),
                apiKey,
                accessToken
        );
    }
}
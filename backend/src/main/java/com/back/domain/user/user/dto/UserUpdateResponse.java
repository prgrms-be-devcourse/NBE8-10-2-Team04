package com.back.domain.user.user.dto;

import com.back.domain.user.user.entity.User;

public record UserUpdateResponse(
        Long id,
        String loginId,
        String email
) {
    /**
     * 수정된 User Entity를 Response DTO로 변환
     *
     * @param user 수정된 User 엔티티
     * @return UserUpdateResponse DTO
     */
    public static UserUpdateResponse from(User user) {
        return new UserUpdateResponse(
                user.getId(),
                user.getLoginId(),
                user.getEmail()
        );
    }
}
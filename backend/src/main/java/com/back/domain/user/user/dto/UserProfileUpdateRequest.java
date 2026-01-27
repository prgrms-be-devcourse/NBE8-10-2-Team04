package com.back.domain.user.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 프로필(이메일) 수정 요청
 * PATCH /api/v1/user/me
 */
public record UserProfileUpdateRequest(
        @NotBlank
        @Size(min = 2, max = 30)
        @Email
        String email
) {
}

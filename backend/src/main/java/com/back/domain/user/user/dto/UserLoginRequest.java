package com.back.domain.user.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequest(
        @NotBlank
        @Size(min = 2, max = 30)
        String loginId,
        @NotBlank
        @Size(min = 2, max = 30)
        String password
) {
}

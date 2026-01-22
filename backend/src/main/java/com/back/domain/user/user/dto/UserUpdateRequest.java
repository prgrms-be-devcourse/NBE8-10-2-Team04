package com.back.domain.user.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank
        @Size(min = 2, max = 30)
        @Email
        String email,
        @NotBlank
        @Size(min = 2, max = 20)
        String password
) {
}

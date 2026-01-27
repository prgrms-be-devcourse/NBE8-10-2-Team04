package com.back.domain.category.category.dto;

public record CategoryResponse(
        Long id,
        String name,
        Long itemCount
) {
}

package com.back.domain.item.item.dto;

public record ItemUpdateRequest(
        Long categoryId,
        String name,
        String imgUrl,
        String cycleDays,
        Boolean isActive
) {
}

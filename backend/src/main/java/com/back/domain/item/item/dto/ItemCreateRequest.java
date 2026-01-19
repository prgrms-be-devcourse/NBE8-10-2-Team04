package com.back.domain.item.item.dto;

import java.time.LocalDate;

public record ItemCreateRequest(
        Long categoryId,
        String name,
        String imgUrl,
        LocalDate startDate,
        String cycleDays
) {
}
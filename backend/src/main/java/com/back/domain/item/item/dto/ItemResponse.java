package com.back.domain.item.item.dto;

import com.back.domain.item.item.entity.Item;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ItemResponse(
        Long id,
        Long userId,
        Long categoryId,
        String name,
        String imgUrl,
        LocalDate startDate,
        String cycleDays,
        LocalDate nextReplacementDate,
        Boolean isActive,
        long dDay
) {
    public ItemResponse(Item item) {
        this(
                item.getId(),
                item.getUserId(),
                item.getCategory() == null ? null : item.getCategory().getId(),
                item.getName(),
                item.getImgUrl(),
                item.getStartDate(),
                item.getCycleDays(),
                item.getNextReplacementDate(),
                item.getIsActive(),
                item.getNextReplacementDate() == null
                        ? 0
                        : ChronoUnit.DAYS.between(LocalDate.now(), item.getNextReplacementDate())
        );
    }
}

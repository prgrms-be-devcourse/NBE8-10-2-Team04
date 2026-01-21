package com.back.domain.item.item.dto;

import com.back.domain.item.item.entity.Item;

import java.time.LocalDate;

public record ItemUpdateResponse(
        Long id,
        Long userId,
        Long categoryId,
        String name,
        String imgUrl,
        LocalDate startDate,
        String cycleDays,
        LocalDate nextReplacementDate,
        Boolean isActive
) {
    public ItemUpdateResponse(Item item) {
        this(
                item.getId(),
                item.getUserId(),
                item.getCategory().getId(),
                item.getName(),
                item.getImgUrl(),
                item.getStartDate(),
                item.getCycleDays(),
                item.getNextReplacementDate(),
                item.getIsActive()
        );
    }
}

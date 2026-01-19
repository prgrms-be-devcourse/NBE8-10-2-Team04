package com.back.domain.item.item.dto;

import com.back.domain.item.item.entity.Item;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ItemResponse(
        Long id,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("category_id")
        Long categoryId,

        String name,

        @JsonProperty("img_url")
        String imgUrl,

        @JsonProperty("start_date")
        LocalDate startDate,

        @JsonProperty("cycle_days")
        String cycleDays,

        @JsonProperty("next_replacement_date")
        LocalDate nextReplacementDate,

        @JsonProperty("is_active")
        Boolean isActive,

        @JsonProperty("d_day")
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

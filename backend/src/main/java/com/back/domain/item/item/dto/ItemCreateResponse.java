package com.back.domain.item.item.dto;

import com.back.domain.item.item.entity.Item;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record ItemCreateResponse(
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
        Boolean isActive
) {
    public ItemCreateResponse(Item item) {
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
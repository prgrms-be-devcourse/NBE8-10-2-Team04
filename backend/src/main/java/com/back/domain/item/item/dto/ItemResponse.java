package com.back.domain.item.item.dto;

import com.back.domain.item.item.entity.Item;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ItemResponse(
        Long id,
        Long user_id,
        Long category_id,
        String name,
        String img_url,
        LocalDate start_date,
        String cycle_days,
        LocalDate next_replacement_date,
        Boolean is_active,
        long d_day
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

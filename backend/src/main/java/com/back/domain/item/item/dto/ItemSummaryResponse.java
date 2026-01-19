package com.back.domain.item.item.dto;

import com.back.domain.item.item.entity.Item;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ItemSummaryResponse(
        Long id,
        String name,
        LocalDate next_replacement_date,
        String img_url,
        long d_day,
        Boolean is_active
) {
    public ItemSummaryResponse(Item item) {
        this(
                item.getId(),
                item.getName(),
                item.getNextReplacementDate(),
                item.getImgUrl(),
                item.getNextReplacementDate() == null
                        ? 0
                        : ChronoUnit.DAYS.between(LocalDate.now(), item.getNextReplacementDate()),
                item.getIsActive()
        );
    }
}

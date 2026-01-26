package com.back.domain.item.itemHistory.dto;

import com.back.domain.item.itemHistory.entity.ItemHistory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ItemHistoryResponse(
        Long id,
        LocalDate startDate,
        Long usedDays
) {
    public static ItemHistoryResponse from(ItemHistory history) {
        return new ItemHistoryResponse(
                history.getId(),
                history.getStartDate(),
                history.getUsedDays()
        );
    }
}

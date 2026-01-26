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
        Long usedDays = null;
        if (history.getEndDate() != null) {
            usedDays = Math.max(
                    0,
                    ChronoUnit.DAYS.between(history.getStartDate(), history.getEndDate())
            );
        }
        return new ItemHistoryResponse(
                history.getId(),
                history.getStartDate(),
                usedDays
        );
    }
}

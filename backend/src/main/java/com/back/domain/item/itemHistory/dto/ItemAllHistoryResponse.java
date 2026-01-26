package com.back.domain.item.itemHistory.dto;

import com.back.domain.item.itemHistory.entity.ItemHistory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ItemAllHistoryResponse(
        Long id,
        Long itemId,
        String itemName,
        LocalDate startDate,
        Long usedDays
) {
    public static ItemAllHistoryResponse from(ItemHistory history) {
        Long usedDays = null;
        if (history.getEndDate() != null) {
            usedDays = Math.max(
                    0,
                    ChronoUnit.DAYS.between(history.getStartDate(), history.getEndDate())
            );
        }

        return new ItemAllHistoryResponse(
                history.getId(),
                history.getItem().getId(),
                history.getItem().getName(),
                history.getStartDate(),
                usedDays);
    }
}

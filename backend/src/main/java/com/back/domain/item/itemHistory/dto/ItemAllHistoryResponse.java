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
        return new ItemAllHistoryResponse(
                history.getId(),
                history.getItem().getId(),
                history.getItem().getName(),
                history.getStartDate(),
                history.getUsedDays()
        );
    }
}

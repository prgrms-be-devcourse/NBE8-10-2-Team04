package com.back.domain.item.itemHistory.dto;

import com.back.domain.item.itemHistory.entity.ItemHistory;

import java.time.LocalDate;

public record ItemAllHistoryResponse(
        Long id,
        Long itemId,
        String itemName,
        String categoryName,
        LocalDate startDate,
        Long usedDays
) {
    public static ItemAllHistoryResponse from(ItemHistory history) {
        return new ItemAllHistoryResponse(
                history.getId(),
                history.getItem().getId(),
                history.getItem().getName(),
                history.getItem().getCategory().getName(),
                history.getStartDate(),
                history.getUsedDays()
        );
    }
}

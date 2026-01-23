package com.back.domain.item.itemHistory.dto;

import com.back.domain.item.itemHistory.entity.ItemHistory;

import java.time.LocalDate;

public record ItemAllHistoryResponse(
        Long id,
        Long itemId,
        String itemName,
        LocalDate startDate
) {
    public static ItemAllHistoryResponse from(ItemHistory history) {
        return new ItemAllHistoryResponse(
                history.getId(),
                history.getItem().getId(),
                history.getItem().getName(),
                history.getStartDate());
    }
}

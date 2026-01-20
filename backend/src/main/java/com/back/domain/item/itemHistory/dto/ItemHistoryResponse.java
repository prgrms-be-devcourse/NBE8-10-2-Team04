package com.back.domain.item.itemHistory.dto;

import com.back.domain.item.itemHistory.entity.ItemHistory;

import java.time.LocalDate;

public record ItemHistoryResponse(
        Long id,
        LocalDate startDate
) {
    public static ItemHistoryResponse from(ItemHistory history) {
        return new ItemHistoryResponse(history.getId(), history.getStartDate());
    }
}

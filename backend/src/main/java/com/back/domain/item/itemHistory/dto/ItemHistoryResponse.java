package com.back.domain.item.itemHistory.dto;

import com.back.domain.item.itemHistory.entity.ItemHistory;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ItemHistoryResponse {
    private Long id;
    private LocalDate startDate;

    public ItemHistoryResponse(ItemHistory itemHistory) {
        this.id = itemHistory.getId();
        this.startDate = itemHistory.getStartDate();
    }
}
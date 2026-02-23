package com.back.domain.item.itemHistory.dto;

import com.back.domain.item.itemHistory.entity.ItemHistory;

import java.time.LocalDate;
import java.util.List;

public record ItemHistoryResponse(
        Long id,
        Long itemId,
        LocalDate startDate,
        LocalDate endDate
) {
    /**
     * Entity -> DTO 변환을 위한 정적 팩토리 메서드
     *
     * @param itemHistory 변환할 ItemHistory 엔티티
     * @return ItemHistoryResponse DTO
     */
    public static ItemHistoryResponse from(ItemHistory itemHistory) {
        return new ItemHistoryResponse(
                itemHistory.getId(),
                itemHistory.getItem().getId(),
                itemHistory.getStartDate(),
                itemHistory.getEndDate()
        );
    }

    /**
     * 여러 Entity를 한번에 변환
     *
     * @param itemHistories 변환할 ItemHistory 엔티티 리스트
     * @return ItemHistoryResponse DTO 리스트
     */
    public static List<ItemHistoryResponse> fromList(List<ItemHistory> itemHistories) {
        return itemHistories.stream()
                .map(ItemHistoryResponse::from)
                .toList();
    }
}
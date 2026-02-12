package com.back.domain.item.itemHistory.dto;

import com.back.domain.item.itemHistory.entity.ItemHistory;

import java.time.LocalDate;
import java.util.List;

public record ItemAllHistoryResponse(
        Long id,
        Long itemId,
        String itemName,
        String categoryName,
        String imgUrl,
        LocalDate startDate,
        LocalDate endDate
) {
    /**
     * Entity -> DTO 변환을 위한 정적 팩토리 메서드
     *
     * @param itemHistory 변환할 ItemHistory 엔티티
     * @return ItemAllHistoryResponse DTO
     */
    public static ItemAllHistoryResponse from(ItemHistory itemHistory) {
        return new ItemAllHistoryResponse(
                itemHistory.getId(),
                itemHistory.getItem().getId(),
                itemHistory.getItem().getName(),
                itemHistory.getItem().getCategory() == null ? null : itemHistory.getItem().getCategory().getName(),
                itemHistory.getItem().getImgUrl(),
                itemHistory.getStartDate(),
                itemHistory.getEndDate()
        );
    }

    /**
     * 여러 Entity를 한번에 변환
     *
     * @param itemHistories 변환할 ItemHistory 엔티티 리스트
     * @return ItemAllHistoryResponse DTO 리스트
     */
    public static List<ItemAllHistoryResponse> fromList(List<ItemHistory> itemHistories) {
        return itemHistories.stream()
                .map(ItemAllHistoryResponse::from)
                .toList();
    }
}
package com.back.domain.item.item.dto;

import com.back.domain.item.item.entity.Item;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record ItemListResponse(
        List<ItemSummary> data
) {
    // ✅ record는 이렇게 "static of()"로 만드는 게 가장 안전함
    public static ItemListResponse of(List<Item> items) {
        return new ItemListResponse(
                items.stream()
                        .map(ItemSummary::new)
                        .toList()
        );
    }

    public record ItemSummary(
            Long id,
            String name,
            LocalDate nextReplacementDate,
            String imgUrl,
            long dDay,
            boolean isActive
    ) {
        public ItemSummary(Item item) {
            this(
                    item.getId(),
                    item.getName(),
                    item.getNextReplacementDate(),
                    item.getImgUrl(),
                    item.getNextReplacementDate() == null
                            ? 0
                            : ChronoUnit.DAYS.between(LocalDate.now(), item.getNextReplacementDate()),
                    item.getIsActive() != null && item.getIsActive()
            );
        }
    }
}

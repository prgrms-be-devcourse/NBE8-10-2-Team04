package com.back.domain.item.item.dto;

import com.back.domain.item.item.entity.Item;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ItemSummaryResponse(
        Long id,
        String name,
        String categoryName,
        LocalDate nextReplacementDate,
        LocalDate lastReplacementDate,
        String imgUrl,
        long dDay,
        Boolean isActive
) {
    public ItemSummaryResponse(Item item) {
        this(
                item.getId(),
                item.getName(),
                item.getCategory() == null ? null : item.getCategory().getName(),
                item.getNextReplacementDate(),
                item.getLastReplacementDate(),
                item.getImgUrl(),
                calculateDDay(item),
                item.getIsActive()
        );
    }

    /**
     * D-day 계산 로직
     * - 비활성 상태: 0 반환 (프론트에서 "비활성" 표시)
     * - 활성 상태: 실제 D-day 계산
     */
    private static long calculateDDay(Item item) {
        // 다음 교체일이 없으면 -1 반환
        if (item.getNextReplacementDate() == null) {
            return -1;
        }

        // 항상 실제 D-day 계산
        return ChronoUnit.DAYS.between(LocalDate.now(), item.getNextReplacementDate());
    }
}

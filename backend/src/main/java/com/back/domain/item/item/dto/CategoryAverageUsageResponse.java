package com.back.domain.item.item.dto;

public record CategoryAverageUsageResponse(
        Long categoryId,
        String categoryName,
        Double averageUsageDays
) {
}
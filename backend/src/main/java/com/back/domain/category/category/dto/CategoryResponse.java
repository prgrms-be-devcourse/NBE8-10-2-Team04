package com.back.domain.category.category.dto;

import com.back.domain.category.category.entity.Category;

import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        Long itemCount
) {
    /**
     * Entity -> DTO 변환을 위한 정적 팩토리 메서드
     *
     * @param category 변환할 Category 엔티티
     * @return CategoryResponse DTO
     */
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                0L  // itemCount는 별도 계산 필요
        );
    }

    /**
     * Entity와 itemCount로 DTO 생성
     *
     * @param category 변환할 Category 엔티티
     * @param itemCount 해당 카테고리의 아이템 수
     * @return CategoryResponse DTO
     */
    public static CategoryResponse of(Category category, Long itemCount) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                itemCount
        );
    }

    /**
     * 여러 Entity를 한번에 변환 (itemCount는 0으로 설정)
     *
     * @param categories 변환할 Category 엔티티 리스트
     * @return CategoryResponse DTO 리스트
     */
    public static List<CategoryResponse> fromList(List<Category> categories) {
        return categories.stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
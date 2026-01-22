package com.back.domain.category.category.dto;

import com.back.domain.category.category.entity.Category;

public record CategoryResponse(
        Long id,
        String name
) {
    public CategoryResponse(Category category) {
        this(category.getId(), category.getName());
    }
}

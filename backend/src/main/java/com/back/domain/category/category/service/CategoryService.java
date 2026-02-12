package com.back.domain.category.category.service;

import com.back.domain.category.category.dto.CategoryResponse;
import com.back.domain.category.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * 특정 사용자의 카테고리별 아이템 개수와 함께 조회
     *
     * @param userId 사용자 ID
     * @return 카테고리 목록 (각 카테고리별 아이템 개수 포함)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllWithItemCount(Long userId) {
        return categoryRepository.findAllWithItemCount(userId);
    }

}

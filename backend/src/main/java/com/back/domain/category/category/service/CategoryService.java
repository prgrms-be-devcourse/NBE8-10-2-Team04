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

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllWithItemCount() {
        return categoryRepository.findAllWithItemCount();
    }

}

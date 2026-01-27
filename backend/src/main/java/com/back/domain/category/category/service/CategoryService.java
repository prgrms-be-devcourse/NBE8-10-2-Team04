package com.back.domain.category.category.service;

import com.back.domain.category.category.dto.CategoryResponse;
import com.back.domain.category.category.entity.Category;
import com.back.domain.category.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public long count() {
        return categoryRepository.count();
    }

    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    //BaseInitData용
    @Transactional
    public Category create(String name) {
        Category category = new Category(name);
        return categoryRepository.save(category);
    }

    //카테고리 조회용
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllWithItemCount() {
        return categoryRepository.findAllWithItemCount();
    }

}

package com.back.domain.category.category.controller;

import com.back.domain.category.category.dto.CategoryResponse;
import com.back.domain.category.category.service.CategoryService;
import com.back.domain.user.user.dto.UserDto;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "CategoryController", description = "카테고리 컨트롤러")
public class CategoryController {
    private final CategoryService categoryService;
    private final Rq rq;

    @GetMapping
    @Operation(summary = "카테고리 조회")
    public RsData<List<CategoryResponse>> getCategories() {
        UserDto actor = rq.getActor();

        return new RsData<>(
                "200-1",
                "카테고리 조회 성공",
                categoryService.findAllWithItemCount(actor.id())
        );
    }
}

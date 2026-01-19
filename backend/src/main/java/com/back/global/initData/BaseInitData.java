package com.back.global.initData;

import com.back.domain.category.category.entity.Category;
import com.back.domain.category.category.service.CategoryService;
import com.back.domain.item.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    @Autowired
    @Lazy
    private BaseInitData self;

    private final CategoryService categoryService;
    private final ItemService itemService;

    @Bean
    ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.createDefaultCategory();
            self.initItems();
        };
    }

    // 서버 실행 시 카테고리 생성
    @Transactional
    public void createDefaultCategory() {
        // 이미 카테고리가 있으면 스킵 (원하면 조건 변경 가능)
        if (categoryService.count() > 0) return;

        categoryService.create("욕실");
        categoryService.create("주방");
        categoryService.create("생활가전");
        categoryService.create("자동차");
    }

    @Transactional
    public void initItems() {
        // 이미 아이템이 있으면 스킵
        if (itemService.count() > 0) return;

        // TODO: User 엔티티 붙으면 userId 대신 memberId/user 엔티티로 교체
        Long user1 = 1L;
        Long user2 = 2L;

        Category bathroom = categoryService.findByName("욕실").orElseThrow();
        Category kitchen = categoryService.findByName("주방").orElseThrow();
        Category car = categoryService.findByName("자동차").orElseThrow();


        itemService.create(
                user1,
                bathroom,
                "칫솔",
                "https://example.com/toothbrush.png",
                LocalDate.of(2026, 1, 1),
                "90",
                LocalDate.of(2026, 4, 1),
                true
        );

        itemService.create(
                user1,
                kitchen,
                "수세미",
                "https://example.com/sponge.png",
                LocalDate.of(2026, 1, 5),
                "30",
                LocalDate.of(2026, 2, 4),
                true
        );

        itemService.create(
                user2,
                car,
                "엔진오일",
                "https://example.com/engineoil.png",
                LocalDate.of(2025, 12, 1),
                "180",
                LocalDate.of(2026, 5, 30),
                true
        );
    }
}

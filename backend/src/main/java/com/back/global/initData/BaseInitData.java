package com.back.global.initData;

import com.back.domain.category.category.entity.Category;
import com.back.domain.category.category.service.CategoryService;
import com.back.domain.item.item.service.ItemService;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.service.UserService;
import com.back.global.app.AppConfig;
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
    private final UserService userService;

    @Bean
    ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.createDefaultCategory();
            self.createDefaultUsers();
            self.initItems();
        };
    }

    // 서버 실행 시 카테고리 생성
    @Transactional
    public void createDefaultCategory() {
        // 이미 카테고리가 있으면 스킵 (원하면 조건 변경 가능)
        if (categoryService.count() > 0) return;

        categoryService.create("집/생활");
        categoryService.create("욕실");
        categoryService.create("주방");
        categoryService.create("뷰티");
        categoryService.create("반려동물");
        categoryService.create("자동차");
        categoryService.create("전자기기");
        categoryService.create("업무");
    }

    @Transactional
    public void createDefaultUsers() {
        // 이미 있으면 스킵, 없으면 생성
        userService.findByLoginId("user1")
                .orElseGet(() -> userService.join("user1", "1234", "hhyukk1273@gmail.com"));

        userService.findByLoginId("user2")
                .orElseGet(() -> userService.join("user2", "1234", "user2@test.com"));
    }

    @Transactional
    public void initItems() {
        // 이미 아이템이 있으면 스킵
        if (itemService.count() > 0) return;

        // TODO: User 엔티티 붙으면 userId 대신 userId/user 엔티티로 교체
        User user1 = userService.findByLoginId("user1").orElseThrow();
        User user2 = userService.findByLoginId("user2").orElseThrow();

        Category bathroom = categoryService.findByName("욕실").orElseThrow();
        Category kitchen = categoryService.findByName("주방").orElseThrow();
        Category car = categoryService.findByName("자동차").orElseThrow();


        itemService.create(
                user1.getId(),
                bathroom,
                "칫솔",
                "https://example.com/toothbrush.png",
                LocalDate.of(2026, 1, 1),
                "3m",
                LocalDate.of(2026, 4, 1),
                true
        );

        itemService.create(
                user1.getId(),
                kitchen,
                "수세미",
                "https://example.com/sponge.png",
                LocalDate.of(2026, 1, 5),
                "1m",
                LocalDate.of(2026, 2, 5),
                true
        );

        itemService.create(
                user2.getId(),
                car,
                "엔진오일",
                "https://example.com/engineoil.png",
                LocalDate.of(2025, 12, 1),
                "6m",
                LocalDate.of(2026, 5, 30),
                true
        );

        // 테스트용: 교체일이 오늘인 아이템 추가
        itemService.create(
                user1.getId(),
                bathroom,
                "테스트용 칫솔 (D-Day 0)",
                "https://example.com/test-toothbrush.png",
                LocalDate.now().minusMonths(3),  // 3개월 전에 시작
                "3m",
                LocalDate.now(),  // 오늘이 교체일
                true
        );
    }

    @Transactional
    public void work1() {
        if (userService.count() > 0) return;

        User user1 = userService.join("user1", "1234", "유저1");
        if (AppConfig.isNotProd()) user1.modifyApiKey(user1.getLoginId());
    }
}

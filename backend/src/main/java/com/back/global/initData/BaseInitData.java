package com.back.global.initData;

import com.back.domain.category.category.entity.Category;
import com.back.domain.category.category.service.CategoryService;
import com.back.domain.item.item.service.ItemService;
import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
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
    private final MemberService memberService;

    @Bean
    ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.createDefaultCategory();
            self.createDefaultMembers();
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
    public void createDefaultMembers() {
        // 이미 있으면 스킵, 없으면 생성
        memberService.findByLoginId("user1")
                .orElseGet(() -> memberService.join("user1", "1234", "user1@test.com"));

        memberService.findByLoginId("user2")
                .orElseGet(() -> memberService.join("user2", "1234", "user2@test.com"));
    }

    @Transactional
    public void initItems() {
        // 이미 아이템이 있으면 스킵
        if (itemService.count() > 0) return;

        // TODO: User 엔티티 붙으면 userId 대신 memberId/user 엔티티로 교체
        Member member1 = memberService.findByLoginId("user1").orElseThrow();
        Member member2 = memberService.findByLoginId("user2").orElseThrow();

        Category bathroom = categoryService.findByName("욕실").orElseThrow();
        Category kitchen = categoryService.findByName("주방").orElseThrow();
        Category car = categoryService.findByName("자동차").orElseThrow();


        itemService.create(
                member1.getId(),
                bathroom,
                "칫솔",
                "https://example.com/toothbrush.png",
                LocalDate.of(2026, 1, 1),
                "3m",
                LocalDate.of(2026, 4, 1),
                true
        );

        itemService.create(
                member1.getId(),
                kitchen,
                "수세미",
                "https://example.com/sponge.png",
                LocalDate.of(2026, 1, 5),
                "1m",
                LocalDate.of(2026, 2, 5),
                true
        );

        itemService.create(
                member2.getId(),
                car,
                "엔진오일",
                "https://example.com/engineoil.png",
                LocalDate.of(2025, 12, 1),
                "6m",
                LocalDate.of(2026, 5, 30),
                true
        );
    }

    @Transactional
    public void work1() {
        if (memberService.count() > 0) return;

        Member memberUser1 = memberService.join("user1", "1234", "유저1");
        if (AppConfig.isNotProd()) memberUser1.modifyApiKey(memberUser1.getLoginId());
    }
}

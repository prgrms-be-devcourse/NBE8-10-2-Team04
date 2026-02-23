package com.back.domain.item.itemHistory.controller;

import com.back.domain.category.category.entity.Category;
import com.back.domain.category.category.repository.CategoryRepository;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.repository.ItemRepository;
import com.back.domain.item.itemHistory.service.ItemHistoryService;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class ItemHistoryControllerTest {

    @Autowired MockMvc mvc;
    @Autowired UserService userService;
    @Autowired ItemRepository itemRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired ItemHistoryService itemHistoryService;

    private User user;
    private Item item;

    // 로그인 후 인증 쿠키를 발급받는 헬퍼 메서드
    private Cookie loginAndGetCookie(String loginId, String password) throws Exception {
        ResultActions result = mvc.perform(
                post("/api/v1/user/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "%s",
                                    "password": "%s"
                                }
                                """.formatted(loginId, password))
        );
        Cookie cookie = result.andReturn().getResponse().getCookie("accessToken");
        assert cookie != null;
        return cookie;
    }

    // 각 테스트 실행 전 기본 사용자, 카테고리, 아이템 데이터 초기화
    @BeforeEach
    void setUp() {
        user = userService.join("historyUser", "1234", "history@test.com");
        Category category = categoryRepository.save(new Category("칫솔"));
        item = itemRepository.save(new Item(
                user, category, "테스트 칫솔", "https://img.example.com/test.jpg",
                LocalDate.of(2024, 1, 1), "30", LocalDate.of(2024, 1, 31), true
        ));
    }

    @Test
    @DisplayName("전체 이력 조회 - 성공: 이력이 없어도 빈 배열로 응답")
    void getAllHistories_empty() throws Exception {
        // 로그인 쿠키 획득
        Cookie cookie = loginAndGetCookie("historyUser", "1234");

        // 이력이 없는 상태에서 전체 조회 요청 시 200 OK와 빈 배열 반환 검증
        mvc.perform(get("/api/v1/items/histories").cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("전체 이력 조회 - 성공: 이력 존재 시 데이터 반환")
    void getAllHistories_withData() throws Exception {
        // 아이템 이력 생성
        itemHistoryService.createItemHistory(item);
        Cookie cookie = loginAndGetCookie("historyUser", "1234");

        // 조회 요청 시 생성된 이력 데이터가 올바르게 반환되는지 검증
        mvc.perform(get("/api/v1/items/histories").cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].itemName").value(item.getName()))
                .andExpect(jsonPath("$.data[0].categoryName").value(item.getCategory().getName()))
                .andExpect(jsonPath("$.data[0].startDate").value(item.getStartDate().toString()));
    }

    @Test
    @DisplayName("전체 이력 조회 - 성공: 다른 유저의 이력은 포함되지 않는다")
    void getAllHistories_isolatedByUser() throws Exception {
        // 다른 유저 및 해당 유저의 아이템 이력 생성
        User other = userService.join("otherUser", "1234", "other@test.com");
        Category otherCategory = categoryRepository.save(new Category("기타 카테고리"));
        Item otherItem = itemRepository.save(new Item(
                other, otherCategory, "다른 칫솔", null,
                LocalDate.of(2024, 2, 1), "30", LocalDate.of(2024, 3, 2), true
        ));
        itemHistoryService.createItemHistory(otherItem);

        // 내 아이템 이력 생성
        itemHistoryService.createItemHistory(item);

        Cookie cookie = loginAndGetCookie("historyUser", "1234");

        // 내 이력만 조회되고 다른 유저의 이력은 포함되지 않는지 검증 (데이터 격리)
        mvc.perform(get("/api/v1/items/histories").cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].itemName").value(item.getName()));
    }

    @Test
    @DisplayName("전체 이력 조회 - 실패: 비로그인 시 403 Forbidden")
    void getAllHistories_unauthorized() throws Exception {
        // 쿠키 없이 요청 시 403 에러 발생 검증
        mvc.perform(get("/api/v1/items/histories"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("특정 아이템 이력 조회 - 성공: 이력이 없으면 빈 배열 반환")
    void getItemHistories_empty() throws Exception {
        Cookie cookie = loginAndGetCookie("historyUser", "1234");

        // 특정 아이템 ID로 조회했으나 이력이 없을 경우 빈 배열 반환 검증
        mvc.perform(get("/api/v1/items/{itemId}/histories", item.getId()).cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("특정 아이템 이력 조회 - 성공: 이력 존재 시 데이터 반환")
    void getItemHistories_withData() throws Exception {
        // 이력 생성
        itemHistoryService.createItemHistory(item);
        Cookie cookie = loginAndGetCookie("historyUser", "1234");

        // 특정 아이템 조회 시 이력 정보가 올바르게 매핑되는지 검증
        mvc.perform(get("/api/v1/items/{itemId}/histories", item.getId()).cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].itemId").value(item.getId()))
                .andExpect(jsonPath("$.data[0].startDate").value(item.getStartDate().toString()));
    }

    @Test
    @DisplayName("특정 아이템 이력 조회 - 성공: 여러 이력이 startDate 내림차순으로 반환된다")
    void getItemHistories_sorted() throws Exception {
        // 동일 아이템에 대해 2개의 이력 생성
        itemHistoryService.createItemHistory(item);
        itemHistoryService.createItemHistory(item);

        Cookie cookie = loginAndGetCookie("historyUser", "1234");

        // 2개의 데이터가 반환되는지 확인 (내림차순 정렬 로직은 Service 테스트에서 상세 검증)
        mvc.perform(get("/api/v1/items/{itemId}/histories", item.getId()).cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("특정 아이템 이력 조회 - 실패: 존재하지 않는 itemId이면 404 예외 발생")
    void getItemHistories_notExistItem() throws Exception {
        Cookie cookie = loginAndGetCookie("historyUser", "1234");

        // DB에 없는 ID 조회 시 404 Not Found 에러 발생 검증
        mvc.perform(get("/api/v1/items/{itemId}/histories", 999999L).cookie(cookie))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").exists());
    }

    @Test
    @DisplayName("특정 아이템 이력 조회 - 실패: 다른 유저의 아이템을 조회하면 404/403 예외 발생 (데이터 격리)")
    void getItemHistories_otherUserItem() throws Exception {
        // 다른 유저와 그의 아이템 및 이력 생성
        User other = userService.join("otherUser", "1234", "other@test.com");
        Category otherCategory = categoryRepository.save(new Category("기타"));
        Item otherItem = itemRepository.save(new Item(
                other, otherCategory, "남의 아이템", null,
                LocalDate.now(), "30", LocalDate.now(), true
        ));
        itemHistoryService.createItemHistory(otherItem);

        Cookie cookie = loginAndGetCookie("historyUser", "1234");

        // 내 계정으로 남의 아이템 이력을 조회 시도 시 예외(404 Not Found 등) 발생 검증
        mvc.perform(get("/api/v1/items/{itemId}/histories", otherItem.getId()).cookie(cookie))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").exists());
    }
}
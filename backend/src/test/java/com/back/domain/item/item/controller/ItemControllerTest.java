package com.back.domain.item.item.controller;

import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.service.ItemService;
import com.back.standard.util.Ut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ItemService itemService;

    // application.yml 에서 주입받는 JWT 비밀키
    @Value("${custom.jwt.secretKey}")
    private String jwtSecret;

    // AccessToken 만료 시간(초 단위)
    @Value("${custom.accessToken.expirationSeconds}")
    private int accessTokenExpirationSeconds;

    /**
     * 테스트용 Access Token 생성 메서드
     * - 실제 로그인 과정을 거치지 않고
     * - 컨트롤러 인증/인가 로직만 검증하기 위해 사용
     */
    private String generateAccessToken(Long userId, String loginId) {
        // JWT Payload(claims)에 들어갈 사용자 정보
        Map<String, Object> claims = Map.of(
                "id", userId,
                "loginId", loginId
        );
        // JWT 생성 (secretKey + 만료시간 + claims)
        return Ut.jwt.toString(jwtSecret, accessTokenExpirationSeconds, claims);
    }

    @Test
    @DisplayName("아이템 교체")
    void replaceItem_success() throws Exception {
        // todo: User 객체로 변경
        Long userId = 1L;
        Long id = 1L;
        Item item = itemService.findById(id).get();

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/items/%d/replace".formatted(id))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("replaceItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.msg").value("아이템 교체 처리 성공"))
                .andExpect(jsonPath("$.data.item.id").value(item.getId()))
                .andExpect(jsonPath("$.data.item.startDate").value(LocalDate.now().toString()));
    }

    @Test
    @DisplayName("아이템 교체 - 작성자가 아닐 때")
    void replaceItem_notOwner() throws Exception {
        // todo: User 객체로 변경
        Long userId = 13L;
        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/items/%d/replace".formatted(id))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("replaceItem"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.msg").value("%d번 아이템에 대한 권한이 없습니다.".formatted(id)));
    }

    @Test
    @DisplayName("아이템 수정")
    void modifyItem_success() throws Exception {
        // todo: User 객체로 변경
        Long userId = 1L;
        Long id = 1L;
        Item item = itemService.findById(id).get();

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/items/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "categoryId": 1,
                                            "name": "수정",
                                            "imgUrl": "edited",
                                            "cycleDays": "6m",
                                            "isActive": true
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("modifyItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.msg").value("아이템 수정 성공"))
                .andExpect(jsonPath("$.data.id").value(item.getId()))
                .andExpect(jsonPath("$.data.userId").value(item.getUser().getId()))
                .andExpect(jsonPath("$.data.categoryId").value(item.getCategory().getId()))
                .andExpect(jsonPath("$.data.name").value(item.getName()))
                .andExpect(jsonPath("$.data.imgUrl").value(item.getImgUrl()))
                .andExpect(jsonPath("$.data.startDate").value(item.getStartDate().toString()))
                .andExpect(jsonPath("$.data.cycleDays").value(item.getCycleDays()))
                .andExpect(jsonPath("$.data.nextReplacementDate").value(item.getNextReplacementDate().toString()))
                .andExpect(jsonPath("$.data.isActive").value(item.getIsActive()));
    }

    @Test
    @DisplayName("아이템 수정 - 작성자가 아닐 때")
    void modifyItem_notOwner() throws Exception {
        // todo: User 객체로 변경
        Long userId = 1L;
        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/items/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "categoryId": 1234,
                                            "name": "수정",
                                            "imgUrl": "edited",
                                            "cycleDays": "6m",
                                            "isActive": true
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("modifyItem"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.msg").value("%d번 아이템에 대한 권한이 없습니다.".formatted(id)));
    }

    @Test
    @DisplayName("아이템 수정 - 존재하지 않는 카테고리")
    void modifyItem_categoryNotFound() throws Exception {
        // todo: User 객체로 변경
        Long userId = 1L;
        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/items/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "categoryId": 1234,
                                            "name": "수정",
                                            "imgUrl": "edited",
                                            "cycleDays": "6m",
                                            "isActive": true
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("modifyItem"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 카테고리입니다."));
    }


    @Test
    @DisplayName("아이템 수정 - 유효하지 않은 주기 입력")
    void modifyItem_InvalidCycleDate() throws Exception {
        // todo: User 객체로 변경
        Long userId = 1L;
        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/items/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "categoryId": 1,
                                            "name": "수정",
                                            "imgUrl": "edited",
                                            "cycleDays": "a1",
                                            "isActive": true
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("modifyItem"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("cycleDays 형식이 올바르지 않습니다. 예: 30d, 2m, 1y"));
    }

    @Test
    @DisplayName("아이템 등록 - 성공")
    void createItem_success() throws Exception {
        // 인증이 필요한 API이므로, 테스트용 Access Token 생성
        String accessToken = generateAccessToken(1L, "user1");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .content("""
                                        {
                                            "categoryId": 1,
                                            "name": "칫솔",
                                            "imgUrl": "https://example.com/toothbrush.jpg",
                                            "startDate": "2025-01-01",
                                            "cycleDays": "90d"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("아이템 등록 성공"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.categoryId").value(1))
                .andExpect(jsonPath("$.data.name").value("칫솔"))
                .andExpect(jsonPath("$.data.imgUrl").value("https://example.com/toothbrush.jpg"))
                .andExpect(jsonPath("$.data.startDate").value("2025-01-01"))
                .andExpect(jsonPath("$.data.cycleDays").value("90d"))
                .andExpect(jsonPath("$.data.nextReplacementDate").value("2025-04-01"))
                .andExpect(jsonPath("$.data.isActive").value(true));
    }

    @Test
    @DisplayName("아이템 등록 - 월 단위 주기")
    void createItem_withMonthCycle() throws Exception {
        String accessToken = generateAccessToken(1L, "user1");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .content("""
                                        {
                                            "categoryId": 2,
                                            "name": "필터",
                                            "imgUrl": "https://example.com/filter.jpg",
                                            "startDate": "2025-01-15",
                                            "cycleDays": "6m"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.data.cycleDays").value("6m"))
                .andExpect(jsonPath("$.data.startDate").value("2025-01-15"))
                .andExpect(jsonPath("$.data.nextReplacementDate").value("2025-07-15"));
    }

    @Test
    @DisplayName("아이템 등록 - 년 단위 주기")
    void createItem_withYearCycle() throws Exception {
        String accessToken = generateAccessToken(1L, "user1");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .content("""
                                        {
                                            "categoryId": 3,
                                            "name": "매트리스",
                                            "imgUrl": "https://example.com/mattress.jpg",
                                            "startDate": "2024-01-01",
                                            "cycleDays": "1y"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.data.cycleDays").value("1y"))
                .andExpect(jsonPath("$.data.nextReplacementDate").value("2025-01-01"));
    }

    @Test
    @DisplayName("아이템 등록 실패 - categoryId 누락")
    void createItem_missingCategoryId() throws Exception {
        String accessToken = generateAccessToken(1L, "user1");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .content("""
                                        {
                                            "name": "칫솔",
                                            "imgUrl": "https://example.com/toothbrush.jpg",
                                            "cycleDays": "90d"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아이템 등록 실패 - name 누락")
    void createItem_missingName() throws Exception {
        String accessToken = generateAccessToken(1L, "user1");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .content("""
                                        {
                                            "categoryId": 1,
                                            "imgUrl": "https://example.com/toothbrush.jpg",
                                            "cycleDays": "90d"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아이템 등록 실패 - cycleDays 누락")
    void createItem_missingCycleDays() throws Exception {
        String accessToken = generateAccessToken(1L, "user1");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .content("""
                                        {
                                            "categoryId": 1,
                                            "name": "칫솔",
                                            "imgUrl": "https://example.com/toothbrush.jpg"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아이템 등록 실패 - 잘못된 cycleDays 형식")
    void createItem_invalidCycleDaysFormat() throws Exception {
        String accessToken = generateAccessToken(1L, "user1");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .content("""
                                        {
                                            "categoryId": 1,
                                            "name": "칫솔",
                                            "imgUrl": "https://example.com/toothbrush.jpg",
                                            "cycleDays": "invalid"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("cycleDays 형식이 올바르지 않습니다. 예: 30d, 2m, 1y"));
    }

    @Test
    @DisplayName("아이템 등록 실패 - 존재하지 않는 카테고리")
    void createItem_categoryNotFound() throws Exception {
        String accessToken = generateAccessToken(1L, "user1");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .content("""
                                        {
                                            "categoryId": 9999,
                                            "name": "칫솔",
                                            "imgUrl": "https://example.com/toothbrush.jpg",
                                            "cycleDays": "90d"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"));
    }

    @Test
    @DisplayName("아이템 등록 실패 - cycleDays 값이 0 이하")
    void createItem_invalidCycleDaysValue() throws Exception {
        String accessToken = generateAccessToken(1L, "user1");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .content("""
                                        {
                                            "categoryId": 1,
                                            "name": "칫솔",
                                            "imgUrl": "https://example.com/toothbrush.jpg",
                                            "cycleDays": "0d"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("cycleDays 값은 1 이상이어야 합니다."));
    }

    @Test
    @DisplayName("아이템 등록 - imgUrl 없이 등록")
    void createItem_withoutImgUrl() throws Exception {
        String accessToken = generateAccessToken(1L, "user1");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .content("""
                                        {
                                            "categoryId": 1,
                                            "name": "칫솔",
                                            "startDate": "2025-01-01",
                                            "cycleDays": "90d"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.data.imgUrl").isEmpty());
    }


}

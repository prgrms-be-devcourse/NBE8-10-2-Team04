package com.back.domain.item.item.controller;

import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.service.ItemService;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.service.UserService;
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
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @Autowired
    private UserService userService;

    private String getAuthHeader(User user) {
        return "Bearer " + user.getApiKey();
    }

    @Test
    @DisplayName("아이템 단건 조회")
    void getItem_success() throws Exception {
        User user = userService.findByLoginId("user1").get();
        Long id = 1L;
        Item item = itemService.findById(id).get();

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/items/" + id)
                                .header("Authorization", getAuthHeader(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("아이템 단건 조회 성공"))
                .andExpect(jsonPath("$.data.id").value(item.getId()))
                .andExpect(jsonPath("$.data.userId").value(user.getId()))
                .andExpect(jsonPath("$.data.categoryName").value(item.getCategory().getName()))
                .andExpect(jsonPath("$.data.dDay").value(ChronoUnit.DAYS.between(LocalDate.now(),
                        item.getNextReplacementDate())));
    }

    @Test
    @DisplayName("아이템 단건 조회 - 없는 아이템")
    void getItem_itemNotFound() throws Exception {
        User user = userService.findByLoginId("user1").get();
        Long id = 99L;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/items/" + id)
                                .header("Authorization", getAuthHeader(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 아이템입니다."));
    }


    @Test
    @DisplayName("아이템 교체")
    void replaceItem_success() throws Exception {
        User user = userService.findByLoginId("user1").get();
        Long id = 1L;
        Item item = itemService.findById(id).get();

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/items/%d/replace".formatted(id))
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findByLoginId("user2").get();
        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/items/%d/replace".formatted(id))
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findByLoginId("user1").get();
        Long id = 1L;
        Item item = itemService.findById(id).get();

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/items/" + id)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findByLoginId("user2").get();
        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/items/" + id)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findByLoginId("user1").get();
        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/items/" + id)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findByLoginId("user1").get();
        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/items/" + id)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findById(1L).orElseThrow();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findById(1L).orElseThrow();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findById(1L).orElseThrow();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findById(1L).orElseThrow();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findById(1L).orElseThrow();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findById(1L).orElseThrow();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findById(1L).orElseThrow();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findById(1L).orElseThrow();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findById(1L).orElseThrow();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", getAuthHeader(user))
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
        User user = userService.findById(1L).orElseThrow();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", getAuthHeader(user))
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

    @Test
    @DisplayName("아이템 삭제 - 성공")
    void deleteItem_success() throws Exception {
        User user = userService.findById(1L).orElseThrow();
        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/items/" + id)
                                .header("Authorization", getAuthHeader(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("deleteItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("아이템 삭제 성공"));
    }

    @Test
    @DisplayName("아이템 삭제 - 작성자가 아닐 때")
    void deleteItem_notOwner() throws Exception {
        User user = userService.findById(2L).orElseThrow();
        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/items/" + id)
                                .header("Authorization", getAuthHeader(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("deleteItem"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.msg").value("%d번 아이템에 대한 권한이 없습니다.".formatted(id)));
    }

    @Test
    @DisplayName("아이템 삭제 - 존재하지 않는 아이템")
    void deleteItem_itemNotFound() throws Exception {
        User user = userService.findById(1L).orElseThrow();
        Long nonExistentId = 9999L;

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/items/" + nonExistentId)
                                .header("Authorization", getAuthHeader(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("deleteItem"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 아이템입니다."));
    }
}

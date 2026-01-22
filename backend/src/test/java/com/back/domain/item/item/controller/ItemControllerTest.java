package com.back.domain.item.item.controller;

import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.service.ItemService;
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
}

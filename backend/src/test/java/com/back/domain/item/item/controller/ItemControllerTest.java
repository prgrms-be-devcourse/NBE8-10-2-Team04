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
    void v1() throws Exception {
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
    void v2() throws Exception {
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("403"))
                .andExpect(jsonPath("$.msg").value("%d번 아이템에 대한 권한이 없습니다.".formatted(id)));
    }
}

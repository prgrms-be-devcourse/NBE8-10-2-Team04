package com.back.domain.item.itemHistory.controller;

import com.back.domain.item.itemHistory.dto.ItemAllHistoryResponse;
import com.back.domain.item.itemHistory.service.ItemHistoryService;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemHistoryControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ItemHistoryService itemHistoryService;
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("전체 아이템 이력 조회")
    void getAllItemHistories_success() throws Exception {
        User user = userService.findByLoginId("user1").get();
        String apiKey = user.getApiKey();

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/items/histories")
                                .header("Authorization", "Bearer " + apiKey)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        List<ItemAllHistoryResponse> list = itemHistoryService.getAllItemHistories(user.getId());

        resultActions
                .andExpect(handler().handlerType(ItemHistoryController.class))
                .andExpect(handler().methodName("getAllItemHistories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.msg").value("전체 아이템 이력 조회 성공"))
                .andExpect(jsonPath("$.data.length()").value(list.size()));

        for (int i = 0; i < list.size(); i++) {
            ItemAllHistoryResponse itemHistory = list.get(i);
            resultActions
                    .andExpect(jsonPath("$.data.[%d].id".formatted(i)).value(itemHistory.id()))
                    .andExpect(jsonPath("$.data.[%d].itemId".formatted(i)).value(itemHistory.itemId()))
                    .andExpect(jsonPath("$.data.[%d].itemName".formatted(i)).value(itemHistory.itemName()))
                    .andExpect(jsonPath("$.data.[%d].startDate".formatted(i)).value(itemHistory.startDate().toString()));
        }
    }
}

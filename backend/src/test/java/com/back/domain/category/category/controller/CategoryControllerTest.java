package com.back.domain.category.category.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("카테고리 조회 - BaseInitData 기본 카테고리 8개 조회 성공")
    void getCategories_success_withBaseInitData() throws Exception {
        mvc.perform(get("/api/v1/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("카테고리 조회 성공"))

                // BaseInitData에서 8개 생성
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(8))))

                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("집/생활"))

                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("욕실"))

                .andExpect(jsonPath("$.data[2].id").value(3))
                .andExpect(jsonPath("$.data[2].name").value("주방"))

                .andExpect(jsonPath("$.data[3].id").value(4))
                .andExpect(jsonPath("$.data[3].name").value("뷰티"))

                .andExpect(jsonPath("$.data[4].id").value(5))
                .andExpect(jsonPath("$.data[4].name").value("반려동물"))

                .andExpect(jsonPath("$.data[5].id").value(6))
                .andExpect(jsonPath("$.data[5].name").value("자동차"))

                .andExpect(jsonPath("$.data[6].id").value(7))
                .andExpect(jsonPath("$.data[6].name").value("전자기기"))

                .andExpect(jsonPath("$.data[7].id").value(8))
                .andExpect(jsonPath("$.data[7].name").value("업무"));
    }
}

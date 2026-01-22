package com.back.domain.user.user.controller;

import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ApiV1UserControllerTest {
    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("회원가입")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/user/signup")
                                .with(csrf()) //
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "loginId": "usernew",
                                            "password": "1234",
                                            "email": "test@test.com"
                                        }
                                        """.stripIndent()) //
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data").exists());

        User user = userService.findByLoginId("usernew").orElseThrow();

        resultActions
                .andExpect(jsonPath("$.data.loginId").value("usernew"));
    }

    @Test
    @DisplayName("로그인")
    void t2() throws Exception {
        // 1. [준비] t1 데이터는 지워졌으므로, t2를 위해 다시 가입시켜야 합니다!
        // join() 결과를 바로 받아서 쓰면 DB 조회 에러 걱정이 없습니다.
        User user = userService.join("usernew", "1234", "test@test.com");

        // 2. [요청]
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/user/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "loginId": "usernew",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                )
                .andDo(print());

        // 3. [검증]
        // user 변수에 이미 정보가 있으므로 findByLoginId를 또 할 필요가 없습니다.

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%s님 환영합니다.".formatted(user.getLoginId())))
                .andExpect(jsonPath("$.data.item").exists())
                .andExpect(jsonPath("$.data.item.id").value(user.getId()))
                .andExpect(jsonPath("$.data.item.loginId").value(user.getLoginId()))
                .andExpect(jsonPath("$.data.apiKey").value(user.getApiKey()));
    }

    @Test
    @DisplayName("내 정보")
    @WithUserDetails("user1")
    void t3() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/user/me")
                )
                .andDo(print());

        User user = userService.findByLoginId("user1").get();

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

}

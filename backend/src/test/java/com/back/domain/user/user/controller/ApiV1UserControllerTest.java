package com.back.domain.user.user.controller;

import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
                .andExpect(jsonPath("$.data.userDto").exists())
                .andExpect(jsonPath("$.data.userDto.id").value(user.getId()))
                .andExpect(jsonPath("$.data.userDto.loginId").value(user.getLoginId()))
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

    @Test
    @DisplayName("회원정보 수정")
    void t4() throws Exception {
        // 1. [준비] BaseInitData의 user1 사용 (없으면 생성)
        // 회원을 가져오는 것만으로는 토큰이 없으므로, 로그인을 해야 함
        User user = userService.findByLoginId("user1")
                .orElseGet(() -> userService.join("user1", "1234", "user1@test.com"));
        String originalEmail = user.getEmail();
        
        // 2. [준비] 로그인 API 호출하여 쿠키에 accessToken 획득
        ResultActions loginResult = mvc
                .perform(
                        post("/api/v1/user/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "loginId": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                );
        
        // 로그인 응답의 쿠키에서 accessToken 추출
        jakarta.servlet.http.Cookie accessTokenCookie = loginResult
                .andReturn()
                .getResponse()
                .getCookie("accessToken");

        // 3. [요청] 회원정보 수정 (쿠키에 있는 토큰 사용)
        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/user/modify")
                                .with(csrf())
                                .cookie(accessTokenCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "updated@test.com",
                                            "password": "newpassword123"
                                        }
                                        """.stripIndent())
                )
                .andDo(print());

        // 4. [검증] 응답 검증
        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("updateUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-2"))
                .andExpect(jsonPath("$.msg").value("회원정보가 수정되었습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.loginId").value("user1"))
                .andExpect(jsonPath("$.data.email").value("updated@test.com"));

        // 5. [검증] DB에서 실제로 수정되었는지 확인
        User afterUser = userService.findByLoginId("user1").orElseThrow();
        assertNotEquals(originalEmail, afterUser.getEmail(), "이메일이 수정되어야 합니다");
        assertEquals("updated@test.com", afterUser.getEmail(), "이메일이 올바르게 수정되어야 합니다");
    }

}

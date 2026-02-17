package com.back.domain.user.user.controller;

import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.service.UserService;
import jakarta.servlet.http.Cookie;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    // 테스트 편의를 위한 헬퍼 메소드: 로그인 후 accessToken 쿠키를 반환
    private Cookie loginAndGetAccessTokenCookie(String loginId, String password) throws Exception {
        ResultActions loginResult = mvc.perform(
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
        Cookie cookie = loginResult.andReturn().getResponse().getCookie("accessToken");
        assertThat(cookie).isNotNull();
        return cookie;
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void t1() throws Exception {
        // 정상적인 회원가입 요청을 전송
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/user/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "loginId": "usernew",
                                            "password": "1234",
                                            "email": "test@test.com"
                                        }
                                        """)
                )
                .andDo(print());

        // 201 상태코드와 가입된 회원 정보를 검증
        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("usernew님 환영합니다. 회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.data.loginId").value("usernew"))
                .andExpect(jsonPath("$.data.email").value("test@test.com"));
    }

    @Test
    @DisplayName("회원가입 - 실패: 아이디 누락")
    void t1_2() throws Exception {
        // 필수 필드인 loginId를 누락하여 요청
        // Valid 어노테이션에 의해 400 Bad Request가 발생
        mvc.perform(post("/api/v1/user/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "password": "1234",
                                    "email": "test@test.com"
                                }
                                """))
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"));
    }

    @Test
    @DisplayName("회원가입 - 실패: 비밀번호 누락")
    void t1_3() throws Exception {
        // 필수 필드인 password를 누락하여 요청
        mvc.perform(post("/api/v1/user/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "usernew",
                                    "email": "test@test.com"
                                }
                                """))
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"));
    }

    @Test
    @DisplayName("회원가입 - 실패: 이메일 형식 오류")
    void t1_4() throws Exception {
        // 잘못된 이메일 형식을 전송하여 유효성 검사 실패를 유도
        mvc.perform(post("/api/v1/user/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "usernew",
                                    "password": "1234",
                                    "email": "invalid-email"
                                }
                                """))
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"));
    }

    @Test
    @DisplayName("회원가입 - 실패: 중복 아이디")
    void t1_5() throws Exception {
        // 이미 존재하는 아이디를 미리 생성
        userService.join("usernew", "1234", "existing@test.com");

        // 동일한 아이디로 회원가입을 시도하여 409 Conflict 발생을 확인
        mvc.perform(post("/api/v1/user/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "usernew",
                                    "password": "1234",
                                    "email": "new@test.com"
                                }
                                """))
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("409-1"));
    }

    @Test
    @DisplayName("로그인 - 성공: accessToken/apiKey 쿠키 세팅 검증 포함")
    void t2() throws Exception {
        // 테스트를 위한 회원을 생성
        User user = userService.join("usernew", "1234", "test@test.com");

        // 로그인 요청을 전송
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
                                    """)
                )
                .andDo(print());

        // 로그인 성공 시 쿠키(accessToken, apiKey)가 발급되었는지 확인
        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%s님 환영합니다.".formatted(user.getLoginId())))
                .andExpect(jsonPath("$.data.user.id").value(user.getId()))
                .andExpect(jsonPath("$.data.user.loginId").value(user.getLoginId()))
                .andExpect(jsonPath("$.data.apiKey").value(user.getApiKey()))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("apiKey"))
                .andExpect(cookie().value("apiKey", user.getApiKey()));
    }

    @Test
    @DisplayName("로그인 - 실패: 존재하지 않는 아이디")
    void t2_2() throws Exception {
        // DB에 없는 아이디로 로그인을 시도
        mvc.perform(post("/api/v1/user/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "nonexistent",
                                    "password": "1234"
                                }
                                """))
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"));
    }

    @Test
    @DisplayName("로그인 - 실패: 잘못된 비밀번호")
    void t2_3() throws Exception {
        // 회원 생성 후 틀린 비밀번호로 로그인을 시도
        userService.join("usernew", "1234", "test@test.com");

        mvc.perform(post("/api/v1/user/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "usernew",
                                    "password": "wrongpassword"
                                }
                                """))
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"));
    }

    @Test
    @DisplayName("로그아웃 - 성공: accessToken/apiKey 쿠키 삭제 검증")
    void t2_1() throws Exception {
        // 로그인 상태
        userService.join("usernew", "1234", "test@test.com");
        Cookie accessTokenCookie = loginAndGetAccessTokenCookie("usernew", "1234");

        // 로그아웃을 요청
        mvc.perform(
                        post("/api/v1/user/logout")
                                .with(csrf())
                                .cookie(accessTokenCookie)
                )
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("로그아웃 되었습니다."))
                // 두 쿠키가 만료(maxAge=0) 처리되었는지 확인
                .andExpect(cookie().maxAge("accessToken", 0))
                .andExpect(cookie().maxAge("apiKey", 0));
    }

    @Test
    @DisplayName("내 정보 - 성공")
    void t3() throws Exception {
        // 사용자 생성 및 로그인 토큰을 확보
        User user = userService.findByLoginId("user1")
                .orElseGet(() -> userService.join("user1", "1234", "user1@test.com"));

        Cookie accessTokenCookie = loginAndGetAccessTokenCookie("user1", "1234");

        // 내 정보 조회 요청
        mvc.perform(
                        get("/api/v1/user/me")
                                .cookie(accessTokenCookie)
                )
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%s님의 정보입니다.".formatted(user.getLoginId())))
                .andExpect(jsonPath("$.data.id").value(user.getId()))
                .andExpect(jsonPath("$.data.loginId").value("user1"))
                .andExpect(jsonPath("$.data.email").value(user.getEmail()));
    }

    @Test
    @DisplayName("내 정보 - 실패: 비로그인")
    void t3_2() throws Exception {
        // 쿠키 없이 내 정보 조회를 요청하면 Spring Security에 의해 403 Forbidden이 반환
        mvc.perform(get("/api/v1/user/me"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("탈퇴 - 성공: 탈퇴 후 로그인 불가 검증")
    void t_delete_1() throws Exception {
        // 탈퇴할 사용자를 생성하고 로그인
        userService.join("deleteuser", "1234", "delete@test.com");
        Cookie accessTokenCookie = loginAndGetAccessTokenCookie("deleteuser", "1234");

        // 회원 탈퇴 요청
        mvc.perform(
                        delete("/api/v1/user/me")
                                .with(csrf())
                                .cookie(accessTokenCookie)
                )
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("deleteMe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.data.loginId").value("deleteuser"))
                // 탈퇴 시에도 쿠키가 삭제
                .andExpect(cookie().maxAge("accessToken", 0));

        // DB에서 실제로 데이터가 삭제되었는지 확인
        assertThat(userService.findByLoginId("deleteuser")).isEmpty();
    }

    @Test
    @DisplayName("탈퇴 - 실패: 비로그인")
    void t_delete_2() throws Exception {
        // 비로그인 상태로 탈퇴 요청 시 차단
        mvc.perform(
                        delete("/api/v1/user/me")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("이메일 수정 - 성공: 새 accessToken/apiKey 쿠키 갱신 검증")
    void t4() throws Exception {
        // 사용자 생성 및 로그인
        User user = userService.findByLoginId("user1")
                .orElseGet(() -> userService.join("user1", "1234", "user1@test.com"));
        String originalEmail = user.getEmail();
        String originalApiKey = user.getApiKey();
        Cookie accessTokenCookie = loginAndGetAccessTokenCookie("user1", "1234");

        // 이메일 수정 요청 전송
        ResultActions resultActions = mvc
                .perform(
                        patch("/api/v1/user/me")
                                .with(csrf())
                                .cookie(accessTokenCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "updated@test.com"
                                        }
                                        """)
                )
                .andDo(print());

        // 정보 수정 후에는 보안을 위해 쿠키(토큰/API 키)가 재발급
        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("updateProfile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-2"))
                .andExpect(jsonPath("$.msg").value("회원정보가 수정되었습니다."))
                .andExpect(jsonPath("$.data.loginId").value("user1"))
                .andExpect(jsonPath("$.data.email").value("updated@test.com"))
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("apiKey"));

        // DB 정보가 실제로 변경되었는지 확인
        User afterUser = userService.findByLoginId("user1").orElseThrow();
        assertNotEquals(originalEmail, afterUser.getEmail());
        assertEquals("updated@test.com", afterUser.getEmail());
        assertNotEquals(originalApiKey, afterUser.getApiKey());
    }

    @Test
    @DisplayName("이메일 수정 - 실패: 비로그인")
    void t4_2() throws Exception {
        // 비로그인 상태로 수정 요청 시 차단 확인
        mvc.perform(
                        patch("/api/v1/user/me")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "updated@test.com"
                                        }
                                        """)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("이메일 수정 - 실패: 이메일 형식 오류")
    void t4_3() throws Exception {
        // 로그인 상태에서 잘못된 이메일 형식으로 수정 시도
        userService.findByLoginId("user1")
                .orElseGet(() -> userService.join("user1", "1234", "user1@test.com"));
        Cookie accessTokenCookie = loginAndGetAccessTokenCookie("user1", "1234");

        mvc.perform(
                        patch("/api/v1/user/me")
                                .with(csrf())
                                .cookie(accessTokenCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "invalid-email"
                                        }
                                        """)
                )
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("updateProfile"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"));
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공: 기존 토큰 무효화 및 새 쿠키 갱신 검증")
    void t5() throws Exception {
        // 사용자 생성 및 로그인
        User user = userService.findByLoginId("user2")
                .orElseGet(() -> userService.join("user2", "1234", "user2@test.com"));
        Cookie oldAccessTokenCookie = loginAndGetAccessTokenCookie("user2", "1234");

        // 비밀번호 변경 요청
        ResultActions resultActions = mvc
                .perform(
                        patch("/api/v1/user/me/password")
                                .with(csrf())
                                .cookie(oldAccessTokenCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "currentPassword": "1234",
                                            "newPassword": "newpassword123"
                                        }
                                        """)
                )
                .andDo(print());

        // 성공 응답 및 쿠키 갱신 확인
        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("changePassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-2"))
                .andExpect(jsonPath("$.msg").value("비밀번호가 변경되었습니다."))
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("apiKey"));

        // 변경된 비밀번호로 로그인이 성공하는지 확인
        mvc.perform(post("/api/v1/user/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "user2",
                                    "password": "newpassword123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"));

        // 기존 비밀번호로 로그인이 실패하는지 확인
        mvc.perform(post("/api/v1/user/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "loginId": "user2",
                                    "password": "1234"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패: 비로그인")
    void t5_2() throws Exception {
        // 비로그인 상태로 요청 시 차단 확인
        mvc.perform(
                        patch("/api/v1/user/me/password")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "currentPassword": "1234",
                                            "newPassword": "newpassword123"
                                        }
                                        """)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패: 현재 비밀번호 불일치")
    void t5_3() throws Exception {
        // 로그인 상태에서 현재 비밀번호를 틀리게 입력했을 때
        userService.findByLoginId("user2")
                .orElseGet(() -> userService.join("user2", "1234", "user2@test.com"));
        Cookie accessTokenCookie = loginAndGetAccessTokenCookie("user2", "1234");

        mvc.perform(
                        patch("/api/v1/user/me/password")
                                .with(csrf())
                                .cookie(accessTokenCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "currentPassword": "wrongpassword",
                                            "newPassword": "newpassword123"
                                        }
                                        """)
                )
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("changePassword"))
                // 서비스 로직에서 403 Forbidden을 반환하는지 확인
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"));
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패: 동일한 비밀번호로 변경 시도")
    void t5_4() throws Exception {
        // 현재 비밀번호와 새 비밀번호가 같을 경우 실패 처리
        userService.findByLoginId("user2")
                .orElseGet(() -> userService.join("user2", "1234", "user2@test.com"));
        Cookie accessTokenCookie = loginAndGetAccessTokenCookie("user2", "1234");

        mvc.perform(
                        patch("/api/v1/user/me/password")
                                .with(csrf())
                                .cookie(accessTokenCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "currentPassword": "1234",
                                            "newPassword": "1234"
                                        }
                                        """)
                )
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("changePassword"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"));
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패: 새 비밀번호 누락")
    void t5_5() throws Exception {
        // 새 비밀번호 필드 누락 시 유효성 검사 실패 확인
        userService.findByLoginId("user2")
                .orElseGet(() -> userService.join("user2", "1234", "user2@test.com"));
        Cookie accessTokenCookie = loginAndGetAccessTokenCookie("user2", "1234");

        mvc.perform(
                        patch("/api/v1/user/me/password")
                                .with(csrf())
                                .cookie(accessTokenCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "currentPassword": "1234"
                                        }
                                        """)
                )
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("changePassword"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"));
    }

    @Test
    @DisplayName("비밀번호 확인 - 성공")
    void t_verify_1() throws Exception {
        // 민감한 정보 접근 전 비밀번호 재확인 성공 케이스
        userService.findByLoginId("user1")
                .orElseGet(() -> userService.join("user1", "1234", "user1@test.com"));
        Cookie accessTokenCookie = loginAndGetAccessTokenCookie("user1", "1234");

        mvc.perform(
                        post("/api/v1/user/me/verify-password")
                                .with(csrf())
                                .cookie(accessTokenCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "password": "1234"
                                        }
                                        """)
                )
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("verifyPassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("비밀번호가 확인되었습니다."));
    }

    @Test
    @DisplayName("비밀번호 확인 - 실패: 비로그인")
    void t_verify_2() throws Exception {
        // 비로그인 상태에서 요청 시 차단 확인
        mvc.perform(
                        post("/api/v1/user/me/verify-password")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "password": "1234"
                                        }
                                        """)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("비밀번호 확인 - 실패: 비밀번호 불일치")
    void t_verify_3() throws Exception {
        // 로그인 상태에서 비밀번호 확인 실패 시 401 Unauthorized 반환 확인
        userService.findByLoginId("user1")
                .orElseGet(() -> userService.join("user1", "1234", "user1@test.com"));
        Cookie accessTokenCookie = loginAndGetAccessTokenCookie("user1", "1234");

        mvc.perform(
                        post("/api/v1/user/me/verify-password")
                                .with(csrf())
                                .cookie(accessTokenCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "password": "wrongpassword"
                                        }
                                        """)
                )
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("verifyPassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"));
    }

    @Test
    @DisplayName("비밀번호 확인 - 실패: 비밀번호 필드 누락")
    void t_verify_4() throws Exception {
        // 비밀번호 필드 누락 시 유효성 검사 실패 확인
        userService.findByLoginId("user1")
                .orElseGet(() -> userService.join("user1", "1234", "user1@test.com"));
        Cookie accessTokenCookie = loginAndGetAccessTokenCookie("user1", "1234");

        mvc.perform(
                        post("/api/v1/user/me/verify-password")
                                .with(csrf())
                                .cookie(accessTokenCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andDo(print())
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("verifyPassword"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"));
    }
}
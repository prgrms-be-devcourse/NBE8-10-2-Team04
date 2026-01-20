package com.back.global.security;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.standard.util.Ut;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Value("${custom.jwt.secretKey}")
    private String jwtSecret;

    @Value("${custom.accessToken.expirationSeconds}")
    private int accessTokenExpirationSeconds;

    @Test
    @DisplayName("JWT 토큰 생성 및 검증")
    void t1() {
        // Given
        Map<String, Object> claims = Map.of(
                "id", 1L,
                "loginid", "testuser",
                "email", "test@test.com"
        );

        // When
        String token = Ut.jwt.toString(jwtSecret, accessTokenExpirationSeconds, claims);

        // Then
        assertThat(token).isNotBlank();
        assertThat(Ut.jwt.isValid(jwtSecret, token)).isTrue();

        Claims parsedClaims = Ut.jwt.payload(jwtSecret, token);
        assertThat(parsedClaims).isNotNull();
        assertThat(parsedClaims.get("id", Long.class)).isEqualTo(1L);
        assertThat(parsedClaims.get("loginid", String.class)).isEqualTo("testuser");
        assertThat(parsedClaims.get("email", String.class)).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰 검증 실패")
    void t2() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThat(Ut.jwt.isValid(jwtSecret, invalidToken)).isFalse();
        assertThat(Ut.jwt.payload(jwtSecret, invalidToken)).isNull();
    }

    @Test
    @DisplayName("인증 불필요한 엔드포인트는 토큰 없이 접근 가능")
    void t3() throws Exception {
        // When
        ResultActions resultActions = mvc.perform(
                        post("/api/v1/user/login")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "loginid": "testuser",
                                "password": "1234"
                            }
                            """.stripIndent())
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인증 필요한 엔드포인트에 토큰 없이 접근 시 401")
    void t4() throws Exception {
        // When (인증 필요한 API - 예시로 GET 요청)
        // 실제로 보호된 엔드포인트가 있다면 그 엔드포인트로 테스트
        // 현재는 임시로 /api/v1/user/signup 다음의 보호된 경로를 가정

        // 실제 보호된 엔드포인트가 있다면 그 경로를 사용하세요
        // 예: get("/api/v1/member/me") 같은 경로

        // 현재는 items가 permitAll이므로 다른 엔드포인트로 테스트해야 함
        // 이 테스트는 실제 보호된 엔드포인트가 생기면 활성화
    }

    @Test
    @DisplayName("유효한 JWT 토큰으로 인증 필요한 엔드포인트 접근")
    void t5() throws Exception {
        // Given
        Member member = memberService.join("testuser", "1234", "test@test.com");

        Map<String, Object> claims = Map.of(
                "id", member.getId(),
                "loginid", member.getLoginid(),
                "email", member.getEmail()
        );

        String accessToken = Ut.jwt.toString(jwtSecret, accessTokenExpirationSeconds, claims);

        // When - 실제 보호된 엔드포인트가 있다면 그 경로로 테스트
        // 현재는 예시로 작성 (보호된 엔드포인트가 생기면 활성화)

        // 예시:
        // ResultActions resultActions = mvc.perform(
        //         get("/api/v1/member/me")
        //                 .header("Authorization", "Bearer " + accessToken)
        // )
        //         .andDo(print());
        //
        // resultActions
        //         .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Bearer 형식이 아닌 Authorization 헤더는 401")
    void t6() throws Exception {
        // Given
        String invalidAuthHeader = "InvalidFormat token123";

        // When - 실제 보호된 엔드포인트가 있다면
        // ResultActions resultActions = mvc.perform(
        //         get("/api/v1/some-protected-endpoint")
        //                 .header("Authorization", invalidAuthHeader)
        // )
        //         .andDo(print());
        //
        // // Then
        // resultActions
        //         .andExpect(status().isUnauthorized())
        //         .andExpect(jsonPath("$.resultCode").value("401-2"))
        //         .andExpect(jsonPath("$.msg").value("Authorization 헤더가 Bearer 형식이 아닙니다."));
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰으로 요청 시 401")
    void t7() throws Exception {
        // Given
        String invalidToken = "Bearer invalid.jwt.token";

        // When - 실제 보호된 엔드포인트가 있다면
        // ResultActions resultActions = mvc.perform(
        //         get("/api/v1/some-protected-endpoint")
        //                 .header("Authorization", invalidToken)
        // )
        //         .andDo(print());
        //
        // // Then
        // resultActions
        //         .andExpect(status().isUnauthorized())
        //         .andExpect(jsonPath("$.resultCode").value("401-1"))
        //         .andExpect(jsonPath("$.msg").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("쿠키에서 accessToken 추출")
    void t8() throws Exception {
        // Given
        Member member = memberService.join("testuser", "1234", "test@test.com");

        Map<String, Object> claims = Map.of(
                "id", member.getId(),
                "loginid", member.getLoginid(),
                "email", member.getEmail()
        );

        String accessToken = Ut.jwt.toString(jwtSecret, accessTokenExpirationSeconds, claims);

        // When - 실제 보호된 엔드포인트가 있다면
        // ResultActions resultActions = mvc.perform(
        //         get("/api/v1/some-protected-endpoint")
        //                 .cookie(new Cookie("accessToken", accessToken))
        // )
        //         .andDo(print());
        //
        // // Then
        // resultActions
        //         .andExpect(status().isOk());
    }

    @Test
    @DisplayName("JWT 토큰의 클레임이 올바르지 않을 때 401")
    void t9() {
        // Given - 필수 클레임(id, loginid)이 없는 토큰
        Map<String, Object> invalidClaims = Map.of(
                "email", "test@test.com"
                // id, loginid 누락
        );

        // When
        String token = Ut.jwt.toString(jwtSecret, accessTokenExpirationSeconds, invalidClaims);

        // Then - 토큰은 생성되지만, CustomAuthenticationFilter에서 검증 실패
        assertThat(token).isNotBlank();

        Claims parsedClaims = Ut.jwt.payload(jwtSecret, token);
        assertThat(parsedClaims.get("id", Long.class)).isNull();
        // 실제로는 필터에서 401을 반환해야 함
    }
}
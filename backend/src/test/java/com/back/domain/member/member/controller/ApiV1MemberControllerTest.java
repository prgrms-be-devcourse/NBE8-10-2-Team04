package com.back.domain.member.member.controller;

import com.back.domain.member.member.dto.MemberDto;
import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ApiV1MemberControllerTest {
    @Autowired
    private MemberService memberService;
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
                                        "loginid": "usernew",
                                        "password": "1234",
                                        "email": "test@test.com"
                                    }
                                    """.stripIndent()) //
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data").exists());

        Member member = memberService.findByLoginid("usernew").orElseThrow();

        resultActions
                .andExpect(jsonPath("$.data.loginid").value("usernew"));
    }

    @Test
    @DisplayName("로그인")
    void t2() throws Exception {
        // 1. [준비] t1 데이터는 지워졌으므로, t2를 위해 다시 가입시켜야 합니다!
        // join() 결과를 바로 받아서 쓰면 DB 조회 에러 걱정이 없습니다.
        Member member = memberService.join("usernew", "1234", "test@test.com");

        // 2. [요청]
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/user/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "loginid": "usernew",
                                        "password": "1234"
                                    }
                                    """.stripIndent())
                )
                .andDo(print());

        // 3. [검증]
        // member 변수에 이미 정보가 있으므로 findByLoginid를 또 할 필요가 없습니다.

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%s님 환영합니다.".formatted(member.getLoginid())))
                .andExpect(jsonPath("$.data.item").exists())
                .andExpect(jsonPath("$.data.item.id").value(member.getId()))
                // DTO 변수명 주의: loginId (대문자 I)
                .andExpect(jsonPath("$.data.item.loginid").value(member.getLoginid()))
                .andExpect(jsonPath("$.data.apiKey").value(member.getApiKey()));
    }

}

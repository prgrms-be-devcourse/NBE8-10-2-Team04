package com.back.global.rq;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.dto.MemberDto;
import com.back.domain.member.member.service.MemberService;
import com.back.global.exception.ServiceException;
import com.back.global.security.SecurityUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final MemberService memberService;

    public MemberDto getActor() {
        return Optional.ofNullable(
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                )
                .filter(Authentication::isAuthenticated)  // 인증 체크 추가
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof SecurityUser)
                .map(principal -> (SecurityUser) principal)
                .map(securityUser -> new MemberDto(  // MemberDto 생성
                        securityUser.getId(),
                        securityUser.getLoginId(),
                        securityUser.getEmail()
                ))
                .orElse(null);
    }

    public String getHeader(String name, String defaultValue) {
        return Optional.ofNullable(req.getHeader(name))
                .filter(v -> !v.isBlank())
                .orElse(defaultValue);
    }


    public String getCookieValue(String name, String defaultValue) {
        return Optional.ofNullable(req.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(c -> c.getName().equals(name))
                        .map(Cookie::getValue)
                        .filter(v -> !v.isBlank())
                        .findFirst())
                .orElse(defaultValue);
    }

    public void setCookie(String name, String value) {
        if (value == null) value = "";

        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        if (value.isBlank()) {
            cookie.setMaxAge(0);
        }

        resp.addCookie(cookie);
    }

    public void deleteCookie(String name) {
        setCookie(name, null);
    }

    public void setHeader(String name, String value) {
        resp.setHeader(name, value);
    }

    // Authorization 헤더에서 JWT 토큰 추출
    // "Bearer {token}" 형식에서 토큰만 추출
    public String getAuthorizationToken() {
        String authorization = req.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7); // "Bearer " 제거
        }
        return null;
    }

    // JWT 토큰에서 사용자 ID 추출
    // Authorization 헤더 또는 쿠키에서 토큰을 찾아 파싱
    public Long getMemberId() {
        // Authorization 헤더에서 토큰 가져오기
        String accessToken = getAuthorizationToken();

        // 헤더에 없으면 쿠키에서 시도
        if (accessToken == null) {
            accessToken = getCookieValue("accessToken", null);
        }

        // 토큰이 없으면 null 반환
        if (accessToken == null) {
            return null;
        }

        // JWT 토큰 파싱
        Map<String, Object> payload = memberService.payload(accessToken);
        if (payload == null) {
            return null;
        }

        // id 추출
        Object idObj = payload.get("id");
        if (idObj instanceof Number n) {
            return n.longValue();
        }
        return null;
    }

    // 현재 로그인한 회원 조회
    public Optional<Member> getMember() {
        Long id = getMemberId();
        if (id == null) {
            return Optional.empty();
        }

        return memberService.findById(id);
    }

    // 로그인 필수 - 사용자 ID 반환 (없으면 예외 발생)
    public Long getRequiredMemberId() {
        Long memberId = getMemberId();
        if (memberId == null) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }
        return memberId;
    }
}
package com.back.global.rq;

import com.back.domain.user.user.dto.UserDto;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.service.UserService;
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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final UserService userService;

    public UserDto getActor() {
        return Optional.ofNullable(
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                )
                .filter(Authentication::isAuthenticated)  // 인증 체크 추가
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof SecurityUser)
                .map(principal -> (SecurityUser) principal)
                .map(securityUser -> new UserDto(  // UserDto 생성
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

    // SecurityContext에서 회원 ID 조회
    public Long getMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }

        Object principal = auth.getPrincipal();

        if (!(principal instanceof SecurityUser)) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }

        return ((SecurityUser) principal).getId();
    }

    // SecurityContext에서 현재 로그인한 회원 조회
    public Optional<User> getMember() {
        Long id = getMemberId();
        return userService.findById(id);
    }
}
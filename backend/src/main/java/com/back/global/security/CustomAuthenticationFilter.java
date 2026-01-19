package com.back.global.security;

import com.back.global.exception.ServiceException;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import com.back.standard.util.Ut;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final Rq rq;

    @Value("${custom.jwt.secretKey}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            work(request, response, filterChain);
        } catch (ServiceException e) {
            RsData<Void> rsData = e.getRsData();
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(rsData.statusCode());
            response.getWriter().write(Ut.json.toString(rsData));
        }
    }

    private void work(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        // 1) API 요청이 아닌 경우 패스
        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) 인증/인가가 필요없는 API 요청 패스 (TODO: 실제 경로로 수정)
        if (List.of(
                "/api/v1/member/login",
                "/api/v1/member/logout",
                "/api/v1/member/join"
        ).contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) accessToken 추출(헤더 우선, 없으면 쿠키)
        String accessToken;
        String headerAuthorization = rq.getHeader("Authorization", "");

        if (!headerAuthorization.isBlank()) {
            if (!headerAuthorization.startsWith("Bearer "))
                throw new ServiceException("401-2", "Authorization 헤더가 Bearer 형식이 아닙니다.");

            accessToken = headerAuthorization.substring("Bearer ".length()).trim();
        } else {
            accessToken = rq.getCookieValue("accessToken", "");
        }

        // accessToken 없으면 통과(이후 SecurityConfig의 authenticated가 막을 것)
        if (accessToken.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4) JWT 검증/파싱 (Claims)
        Claims claims = Ut.jwt.payload(jwtSecret, accessToken);
        if (claims == null) {
            throw new ServiceException("401-1", "유효하지 않은 토큰입니다.");
        }

        // 5) SecurityContext 주입 (Member 엔티티 기준: id, loginid, email)
        Long id = claims.get("id", Long.class);
        String loginid = claims.get("loginid", String.class);
        String email = claims.get("email", String.class);

        if (id == null || loginid == null) {
            throw new ServiceException("401-1", "토큰 클레임이 올바르지 않습니다.");
        }

        UserDetails user = new SecurityUser(
                id,
                loginid,
                "",     // 토큰 인증에서는 비번 검증 안 함
                email,
                List.of() // 권한 없으면 빈 리스트
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities())
        );

        filterChain.doFilter(request, response);

        // TODO(다음 단계): refresh(apiKey)로 사용자 조회 + accessToken 재발급
    }
}
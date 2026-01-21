package com.back.global.security;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final Rq rq;

    @Value("${custom.jwt.secretKey}")
    private String jwtSecret;

    @Value("${custom.accessToken.expirationSeconds}")
    private int accessTokenExpirationSeconds;

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

        // 2) 인증/인가가 필요없는 API 요청 패스
        if (List.of(
                "/api/v1/user/login",
                "/api/v1/user/signup",
                "/api/v1/user/refresh"
        ).contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) Authorization 헤더에서 accessToken 추출
        String accessToken = null;
        String headerAuthorization = rq.getHeader("Authorization", "");

        if (!headerAuthorization.isBlank()) {
            if (!headerAuthorization.startsWith("Bearer ")) {
                throw new ServiceException("401-2", "Authorization 헤더가 Bearer 형식이 아닙니다.");
            }
            accessToken = headerAuthorization.substring("Bearer ".length()).trim();
        } else {
            accessToken = rq.getCookieValue("accessToken", "");
        }

        // accessToken이 없으면 통과 (익명 요청)
        if (accessToken == null || accessToken.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4) accessToken 검증 및 파싱
        Claims claims = Ut.jwt.payload(jwtSecret, accessToken);
        if (claims == null) {
            throw new ServiceException("401-1", "유효하지 않은 토큰입니다.");
        }

        // 5) 토큰에서 회원 ID 추출
        Long id = claims.get("id", Long.class);
        String loginid = claims.get("loginid", String.class);

        if (id == null || loginid == null) {
            throw new ServiceException("401-1", "토큰 클레임이 올바르지 않습니다.");
        }

        // 6) DB에서 실제 회원 조회
        Member member = memberRepository.findById(id)  //
                .orElseThrow(() -> new ServiceException("401-1", "존재하지 않는 회원입니다."));

        // 7) accessToken이 만료되었는지 확인 (선택적 - 만료 시간 체크)
        // 현재는 토큰이 유효하면 통과, 만료되면 위에서 이미 null 반환됨
        
        // 8) accessToken이 유효하지만 만료 시간이 가까우면 새로 발급 (선택적)
        // 필요시 토큰 만료 시간을 체크하여 재발급할 수 있음
        // 현재는 토큰이 유효하면 그대로 사용

        // 9) SecurityContext에 인증 정보 주입
        UserDetails user = new SecurityUser(
                member.getId(),
                member.getLoginid(),
                "",
                member.getEmail() != null ? member.getEmail() : "",
                List.of() // 권한 없으면 빈 리스트
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities())
        );

        filterChain.doFilter(request, response);
    }
}
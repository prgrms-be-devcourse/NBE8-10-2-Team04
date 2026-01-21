package com.back.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFilter CustomAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(

                        auth -> auth
                                // 허용할 요청 설정
                                .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                // 회원가입과 로그인은 누구나 들어갈 수 있게 허용
                                .requestMatchers("/api/v1/user/signup", "/api/v1/user/login").permitAll()
                                // ✅ (개발용) 아이템만 임시 허용
                                .requestMatchers("/api/v1/items/**").permitAll()
                                // ✅ (개발용) 카테고리 임시 허용
                                .requestMatchers("/api/v1/categories/**").permitAll()

                                // 기타 api 요청은 인가 필요 -> 로그인하지 않으면 제한
                                .requestMatchers("/api/*/**").authenticated()
                                // 나머지 요청은 허용
                                .anyRequest().permitAll()
                )
                .headers(
                        headers -> headers
                                .frameOptions(
                                        HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                )
                ).csrf(
                        // CSRF 비활성화
                        AbstractHttpConfigurer::disable
                )
                .addFilterBefore(CustomAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}

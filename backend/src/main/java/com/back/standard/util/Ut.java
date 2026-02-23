package com.back.standard.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@UtilityClass
public class Ut {

    @UtilityClass
    public static class jwt {
        public static boolean isValid(String secret, String jwtStr) {
            try {
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(jwtStr);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public static Claims payload(String secret, String jwtStr) {
            try {
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                return Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(jwtStr)
                        .getPayload();
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * JWT 토큰 생성 (Map 방식)
         * @param secret 시크릿 키
         * @param expireSeconds 만료 시간(초)
         * @param body 클레임 맵
         * @return JWT 토큰 문자열
         */
        public static String toString(String secret, int expireSeconds, Map<String, Object> body) {
            ClaimsBuilder claimsBuilder = Jwts.claims();

            for (Map.Entry<String, Object> entry : body.entrySet()) {
                claimsBuilder.add(entry.getKey(), entry.getValue());
            }

            Claims claims = claimsBuilder.build();

            Date issuedAt = new Date();
            Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            return Jwts.builder()
                    .claims(claims)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();
        }
    }

    @UtilityClass
    public static class json {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static String toString(Object obj) {
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (Exception e) {
                return "{\"resultCode\":\"500-1\",\"msg\":\"json serialize fail\"}";
            }
        }
    }
}
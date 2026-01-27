package com.back.domain.user.user.service;

import com.back.domain.user.user.entity.User;
import com.back.standard.util.Ut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthTokenService {
    @Value("${custom.jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${custom.accessToken.expirationSeconds}")
    private int accessTokenExpirationSeconds;

    String genAccessToken(User user) {
        long id = user.getId();
        String loginId = user.getLoginId();
        String email = user.getEmail();
        Long tokenVersion = user.getTokenVersion();

        return Ut.jwt.toString(
                jwtSecretKey,
                accessTokenExpirationSeconds,
                Map.of("id", id, "loginId", loginId, "email", email, "tokenVersion", tokenVersion)
        );
    }

    Map<String, Object> payload(String accessToken) {
        Map<String, Object> parsedPayload = Ut.jwt.payload(jwtSecretKey, accessToken);

        if (parsedPayload == null) return null;

        long id = ((Number) parsedPayload.get("id")).longValue();
        String loginId = (String) parsedPayload.get("loginId");
        String email = (String) parsedPayload.get("email");
        long tokenVersion = ((Number) parsedPayload.get("tokenVersion")).longValue();

        return Map.of("id", id, "loginId", loginId, "email", email, "tokenVersion", tokenVersion);

    }
}
package com.back.domain.member.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loginId;
    private String password;
    private String email;
    @Column(unique = true)
    private String apiKey;

    public Member(long id, String loginId) {
        this.id = id;
        this.loginId = loginId;
    }

    public Member(String loginId, String password, String email) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.apiKey = UUID.randomUUID().toString();
    }
    public void modifyApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}

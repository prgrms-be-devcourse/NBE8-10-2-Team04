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
    private String loginid;
    private String password;
    private String email;
    @Column(unique = true)
    private String apiKey;

    public Member(long id, String loginid) {
        this.id = id;
        this.loginid = loginid;
    }

    public Member(String loginid, String password, String email) {
        this.loginid = loginid;
        this.password = password;
        this.email = email;
        this.apiKey = UUID.randomUUID().toString();
    }
    public void modifyApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}

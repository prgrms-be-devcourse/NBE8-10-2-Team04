package com.back.domain.user.user.entity;

import com.back.domain.item.item.entity.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loginId;
    private String password;
    private String email;
    @Column(unique = true)
    private String apiKey;

    @Column(nullable = false)
    private Long tokenVersion = 0L;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    public User(long id, String loginId) {
        this.id = id;
        this.loginId = loginId;
    }

    public User(String loginId, String password, String email) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.apiKey = UUID.randomUUID().toString();
    }

    public void modifyApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void modifyUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void increaseTokenVersion() {
        this.tokenVersion++;
    }
}

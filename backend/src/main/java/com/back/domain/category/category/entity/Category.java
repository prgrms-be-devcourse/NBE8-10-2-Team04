package com.back.domain.category.category.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder // 빌더 패턴 사용 가능하게 함
@AllArgsConstructor // 빌더가 모든 필드를 포함한 생성자를 사용할 수 있게 함
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Category(String name) {
        this.name = name;
    }
}

package com.back.domain.item.item.entity;

import com.back.domain.category.category.entity.Category;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: User 엔티티 추가 시 변경 예정
    private Long userId;

    @ManyToOne // category(1) : item(N)
    private Category category;

    private String name;

    private String imgUrl;

    private LocalDate startDate;

    private String cycleDays;

    private LocalDate nextReplacementDate;

    private Boolean isActive;
}

package com.back.domain.item.item.entity;

import com.back.domain.category.category.entity.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private boolean isActive;

    public Item(
            Long userId,
            Category category,
            String name,
            String imgUrl,
            LocalDate startDate,
            String cycleDays,
            LocalDate nextReplacementDate,
            Boolean isActive
    ) {
        this.userId = userId;
        this.category = category;
        this.name = name;
        this.imgUrl = imgUrl;
        this.startDate = startDate;
        this.cycleDays = cycleDays;
        this.nextReplacementDate = nextReplacementDate;
        this.isActive = isActive;
    }
}

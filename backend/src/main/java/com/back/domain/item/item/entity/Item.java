package com.back.domain.item.item.entity;

import com.back.domain.category.category.entity.Category;
import com.back.global.exception.ServiceException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: User 엔티티 추가 시 변경 예정
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY) // category(1) : item(N)
    private Category category;

    private String name;

    private String imgUrl;

    private LocalDate startDate;

    private String cycleDays;

    private LocalDate nextReplacementDate;

    private Boolean isActive;

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

    public void modifyDate(LocalDate startDate, LocalDate nextReplacementDate) {
        this.startDate = startDate;
        this.nextReplacementDate = nextReplacementDate;
    }

    //현재 요청자(actorUserId)가 이 Item의 소유자인지 확인
    public void validateOwner(Long actorUserId) {
        if (!Objects.equals(this.userId, actorUserId)) {
            throw new ServiceException("403-1", "%d번 아이템에 대한 권한이 없습니다.".formatted(this.id));
        }
    }

    public void modify(Category category, String name, String imgUrl, String cycleDays, LocalDate nextReplacementDate
            , Boolean isActive) {
        this.category = category;
        this.name = name;
        this.imgUrl = imgUrl;
        this.cycleDays = cycleDays;
        this.nextReplacementDate = nextReplacementDate;
        this.isActive = isActive;
    }
}

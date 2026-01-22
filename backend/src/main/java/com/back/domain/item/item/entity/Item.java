package com.back.domain.item.item.entity;

import com.back.domain.category.category.entity.Category;
import com.back.domain.item.itemHistory.entity.ItemHistory;
import com.back.domain.user.user.entity.User;
import com.back.global.exception.ServiceException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // user(1) : item(N)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // category(1) : item(N)
    private Category category;

    private String name;

    private String imgUrl;

    private LocalDate startDate;

    private String cycleDays;

    private LocalDate nextReplacementDate;

    private Boolean isActive;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true) // item(1) : item_history(N)
    List<ItemHistory> itemHistories;

    public Item(
            User user,
            Category category,
            String name,
            String imgUrl,
            LocalDate startDate,
            String cycleDays,
            LocalDate nextReplacementDate,
            Boolean isActive
    ) {
        this.user = user;
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
        if (!Objects.equals(this.user.getId(), actorUserId)) {
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

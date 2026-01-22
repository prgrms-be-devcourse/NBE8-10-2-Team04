package com.back.domain.item.itemHistory.entity;

import com.back.domain.item.item.entity.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "item_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // item(1) : item_history(N)
    private Item item;

    private LocalDate startDate;

    public ItemHistory(Item item) {
        this.item = item;
        this.startDate = item.getStartDate();
    }
}

package com.back.domain.item.item.service;

import com.back.domain.category.category.entity.Category;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    //목록조회용
    @Transactional(readOnly = true)
    public List<Item> findAllByUserIdOrderByNextReplacementDateAsc(Long userId) {
        return itemRepository.findAllByUserIdOrderByNextReplacementDateAsc(userId);
    }

    @Transactional(readOnly = true)
    public long count() {
        return itemRepository.count();
    }

    @Transactional
    public Item create(
            Long userId,
            Category category,
            String name,
            String imgUrl,
            LocalDate startDate,
            String cycleDays,
            LocalDate nextReplacementDate,
            Boolean isActive
    ) {
        Item item = new Item(
                userId, category, name, imgUrl,
                startDate, cycleDays, nextReplacementDate, isActive
        );

        return itemRepository.save(item);
    }
}

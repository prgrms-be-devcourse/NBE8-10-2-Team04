package com.back.domain.item.item.service;

import com.back.domain.category.category.entity.Category;
import com.back.domain.category.category.repository.CategoryRepository;
import com.back.domain.item.item.dto.ItemCreateRequest;
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
    private final CategoryRepository categoryRepository;

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

    @Transactional
    public Item createItem(Long userId, ItemCreateRequest request) {
        // Category 조회
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        // nextReplacementDate 계산
        LocalDate nextReplacementDate = calculateNextReplacementDate(
                request.startDate(),
                request.cycleDays()
        );

        return create(
                userId,
                category,
                request.name(),
                request.imgUrl(),
                request.startDate(),
                request.cycleDays(),
                nextReplacementDate,
                true  // 기본값 활성화
        );
    }

    private LocalDate calculateNextReplacementDate(LocalDate startDate, String cycleDays) {
        if (cycleDays.endsWith("d")) {
            int days = Integer.parseInt(cycleDays.substring(0, cycleDays.length() - 1));
            return startDate.plusDays(days);
        } else if (cycleDays.endsWith("m")) {
            int months = Integer.parseInt(cycleDays.substring(0, cycleDays.length() - 1));
            return startDate.plusMonths(months);
        } else if (cycleDays.endsWith("y")) {
            int years = Integer.parseInt(cycleDays.substring(0, cycleDays.length() - 1));
            return startDate.plusYears(years);
        }
        throw new IllegalArgumentException("잘못된 cycleDays 형식입니다.");
    }
}

package com.back.domain.item.item.service;

import com.back.domain.category.category.entity.Category;
import com.back.domain.category.category.repository.CategoryRepository;
import com.back.domain.item.item.dto.ItemCreateRequest;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.repository.ItemRepository;
import com.back.domain.item.item.vo.CyclePeriod;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    //단건조회용
    @Transactional(readOnly = true)
    public Optional<Item> findByIdAndUserId(Long itemId, Long userId) {
        return itemRepository.findByIdAndUserId(itemId, userId);
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
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ServiceException("400-1", "cycleDays는 필수 값입니다."));

        LocalDate startDate = request.resolvedStartDate();
        CyclePeriod cyclePeriod = CyclePeriod.from(request.cycleDays());
        LocalDate nextReplacementDate = cyclePeriod.addTo(startDate);

        return create(
                userId,
                category,
                request.name(),
                request.imgUrl(),
                startDate,
                request.cycleDays(),          // 원문 저장이 필요하면 유지
                nextReplacementDate,
                true
        );
    }
}

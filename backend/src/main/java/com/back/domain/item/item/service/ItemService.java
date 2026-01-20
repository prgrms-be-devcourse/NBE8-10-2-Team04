package com.back.domain.item.item.service;

import com.back.domain.category.category.entity.Category;
import com.back.domain.category.category.repository.CategoryRepository;
import com.back.domain.item.item.dto.ItemCreateRequest;
import com.back.domain.item.item.dto.ItemUpdateRequest;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.repository.ItemRepository;
import com.back.domain.item.item.vo.CyclePeriod;
import com.back.domain.item.itemHistory.service.ItemHistoryService;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemHistoryService itemHistoryService;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    // itemId로 아이템을 조회하고 요청자(userId)가 소유자인지 검증한 뒤 실제 삭제 수행
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        Item item = findItemOrThrow(itemId);
        item.validateOwner(userId);
        itemRepository.delete(item);
    }

    // itemId로 Item 조회
    private Item findItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ServiceException(("400-1"), "존재하지 않는 아이템입니다."));
    }

    //목록조회용
    @Transactional(readOnly = true)
    public List<Item> findAllByUserIdOrderByNextReplacementDateAsc(Long userId) {
        return itemRepository.findAllByUserIdOrderByNextReplacementDateAsc(userId);
    }

    //단건조회용
    @Transactional(readOnly = true)
    public Item findByIdAndUserId(Long itemId, Long userId) {
        return itemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 아이템입니다."));
    }

    //카테고리별 목록조회용
    public List<Item> findAllByUserIdAndCategoryId(Long userId, Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ServiceException("404-1", "존재하지 않는 카테고리입니다.");
        }

        return itemRepository.findAllByUserIdAndCategoryId(userId, categoryId);
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

    public void modifyDate(Item item) {
        // 시작일 변경 : 교체를 요청한 시각
        LocalDate newStartDate = LocalDate.now();

        // 다음 교체일 변경 : 시작일 + 주기
        CyclePeriod cyclePeriod = CyclePeriod.from(item.getCycleDays());
        LocalDate newNextReplacementDate = cyclePeriod.addTo(newStartDate);

        item.modifyDate(newStartDate, newNextReplacementDate);
    }

    @Transactional
    public void replaceItem(Item item) {
        // 아이템 정보 교체
        modifyDate(item);

        // 이력 추가
        itemHistoryService.createItemHistory(item);
    }

    @Transactional
    public void modify(Item item, ItemUpdateRequest request) {

        // 카테고리 존재 여부 확인
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 카테고리입니다."));

        // 주기(cycleDays) 수정 시 다음 교체일도 함께 변경
        LocalDate nextReplacementDate = item.getNextReplacementDate();
        if (Objects.equals(request.cycleDays(), item.getCycleDays())) {
            CyclePeriod cyclePeriod = CyclePeriod.from(request.cycleDays());
            nextReplacementDate = cyclePeriod.addTo(item.getStartDate());
        }

        // 아이템 수정
        item.modify(category, request.name(), request.imgUrl(), request.cycleDays(), newNextReplacementDate,
                request.isActive());
    }
}

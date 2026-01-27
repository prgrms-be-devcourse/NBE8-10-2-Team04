package com.back.domain.item.itemHistory.service;

import com.back.domain.item.item.entity.Item;
import com.back.domain.item.itemHistory.dto.ItemAllHistoryResponse;
import com.back.domain.item.itemHistory.dto.ItemHistoryResponse;
import com.back.domain.item.itemHistory.entity.ItemHistory;
import com.back.domain.item.itemHistory.repository.ItemHistoryRepository;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemHistoryService {
    private final ItemHistoryRepository itemHistoryRepository;

    @Transactional
    public void createItemHistory(Item item) {
        ItemHistory itemHistory = new ItemHistory(item);
        itemHistoryRepository.save(itemHistory);
    }

    @Transactional(readOnly = true)
    public List<ItemHistoryResponse> getItemHistories(Long itemId) {
        return itemHistoryRepository.findByItemIdOrderByStartDateDesc(itemId).stream()
                .map(ItemHistoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemAllHistoryResponse> getAllItemHistories(Long userId) {
        return itemHistoryRepository.findByUserIdOrderByStartDateDesc(userId)
                .stream()
                .map(ItemAllHistoryResponse::from)
                .toList();
    }

    @Transactional
    public void endHistory(Long itemId, LocalDate endDate) {
        ItemHistory ongoing = itemHistoryRepository.findTopByItemIdAndEndDateIsNullOrderByStartDateDesc(itemId)
                .orElseThrow(() -> new ServiceException("404-1", "진행중인 이력이 없습니다."));

        ongoing.end(endDate);
    }
}

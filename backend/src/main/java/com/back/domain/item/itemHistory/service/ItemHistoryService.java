package com.back.domain.item.itemHistory.service;

import com.back.domain.item.item.entity.Item;
import com.back.domain.item.itemHistory.dto.ItemHistoryResponse;
import com.back.domain.item.itemHistory.entity.ItemHistory;
import com.back.domain.item.itemHistory.repository.ItemHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        List<ItemHistory> histories = itemHistoryRepository
                .findByItemIdOrderByStartDateDesc(itemId);

        return histories.stream()
                .map(ItemHistoryResponse::new)
                .collect(Collectors.toList());
    }
}

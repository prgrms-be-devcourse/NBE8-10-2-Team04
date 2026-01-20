package com.back.domain.item.itemHistory.service;

import com.back.domain.item.item.entity.Item;
import com.back.domain.item.itemHistory.entity.ItemHistory;
import com.back.domain.item.itemHistory.repository.ItemHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemHistoryService {
    private final ItemHistoryRepository itemHistoryRepository;

    public void createItemHistory(Item item) {
        ItemHistory itemHistory = new ItemHistory(item);
        itemHistoryRepository.save(itemHistory);
    }
}

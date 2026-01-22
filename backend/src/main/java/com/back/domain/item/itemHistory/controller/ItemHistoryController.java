package com.back.domain.item.itemHistory.controller;

import com.back.domain.item.itemHistory.dto.ItemHistoryResponse;
import com.back.domain.item.itemHistory.service.ItemHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@Tag(name = "ItemHistoryController", description = "아이템 이력 컨트롤러")
public class ItemHistoryController {

    private final ItemHistoryService itemHistoryService;

    @GetMapping
    @GetMapping("/{itemId}/histories")
    @Operation(summary = "아이템 이력 조회", description = "특정 아이템의 이력을 조회합니다.")
    public List<ItemHistoryResponse> getItemHistories(@PathVariable Long itemId) {
        return itemHistoryService.getItemHistories(itemId);
    }
}

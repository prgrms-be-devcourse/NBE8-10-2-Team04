package com.back.domain.item.itemHistory.controller;

import com.back.domain.item.itemHistory.dto.ItemAllHistoryResponse;
import com.back.domain.item.itemHistory.dto.ItemHistoryResponse;
import com.back.domain.item.itemHistory.service.ItemHistoryService;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
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
    private final Rq rq;

    @GetMapping("/histories")
    @Operation(summary = "전체 아이템 이력 조회", description = "전체 아이템의 이력을 일괄 조회합니다.")
    public RsData<List<ItemAllHistoryResponse>> getAllItemHistories() {
        Long userId = rq.getMemberId();

        // Service에서 이미 변환된 DTO 리스트를 반환
        List<ItemAllHistoryResponse> histories = itemHistoryService.getAllItemHistories(userId);

        return new RsData<>("200-1", "전체 아이템 이력 조회 성공", histories);
    }

    @GetMapping("/{itemId}/histories")
    @Operation(summary = "특정 아이템의 교체 이력 조회", description = "특정 아이템의 이력을 조회합니다.")
    public RsData<List<ItemHistoryResponse>> getItemHistories(@PathVariable Long itemId) {
        // Service에서 이미 변환된 DTO 리스트를 반환
        List<ItemHistoryResponse> histories = itemHistoryService.getItemHistories(itemId);

        return new RsData<>("200-1", "아이템 이력 조회 성공", histories);
    }
}

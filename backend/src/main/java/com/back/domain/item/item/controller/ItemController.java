package com.back.domain.item.item.controller;

import com.back.domain.item.item.dto.ItemListResponse;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.service.ItemService;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@Tag(name = "ItemController", description = "아이템 컨트롤러")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "아이템 목록 조회")
    public RsData<ItemListResponse> getItems(
            @RequestParam Long userId
    ) {
        List<Item> items = itemService.findAllByUserIdOrderByNextReplacementDateAsc(userId);

        return new RsData<>(
                "200-1",
                "아이템 목록 조회 성공",
                ItemListResponse.of(items)
        );
    }

}

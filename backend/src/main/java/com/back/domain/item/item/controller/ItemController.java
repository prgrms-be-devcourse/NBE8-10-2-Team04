package com.back.domain.item.item.controller;

import com.back.domain.item.item.dto.ItemCreateRequest;
import com.back.domain.item.item.dto.ItemCreateResponse;
import com.back.domain.item.item.dto.ItemResponse;
import com.back.domain.item.item.dto.ItemSummaryResponse;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.service.ItemService;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@Validated
@Tag(name = "ItemController", description = "아이템 컨트롤러")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @Operation(summary = "아이템 등록")
    public RsData<ItemCreateResponse> createItem(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ItemCreateRequest request
    ) {
        Long userId = 1L; //토큰 추출

        Item item = itemService.createItem(userId, request);

        return new RsData<>(
                "201-1",
                "아이템 등록 성공",
                new ItemCreateResponse(item)
        );
    }

    @GetMapping
    @Operation(summary = "아이템 목록 조회")
    public RsData<List<ItemSummaryResponse>> getItems(
            @RequestParam  @Min(value = 1, message = "userId는 1 이상이어야 합니다.") Long userId
    ) {
        List<Item> items = itemService.findAllByUserIdOrderByNextReplacementDateAsc(userId);

        List<ItemSummaryResponse> data = items.stream()
                .map(ItemSummaryResponse::new)
                .toList();

        return new RsData<>(
                "200-1",
                "아이템 목록 조회 성공",
                data);
    }


    @GetMapping("/{itemId}")
    @Operation(summary = "아이템 단건 조회")
    public RsData<ItemResponse> getItem(
            @PathVariable Long itemId,
            @RequestParam Long userId
    ) {
        Item item = itemService.findByIdAndUserId(itemId, userId);
        return new RsData<>("200-1", "아이템 단건 조회 성공", new ItemResponse(item));
    }


}

package com.back.domain.item.item.controller;

import com.back.domain.item.item.dto.*;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.service.ItemService;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@Validated
@Tag(name = "ItemController", description = "아이템 컨트롤러")
public class ItemController {
    private final ItemService itemService;
    private final Rq rq;

    @DeleteMapping("/{itemId}")
    @Operation(summary = "아이템 삭제")
    public RsData<Void> deleteItem(
            @PathVariable Long itemId
    ) {
        Long userId = rq.getMemberId();

        itemService.deleteItem(userId, itemId);

        return new RsData<>(
                "200-1",
                "아이템 삭제 성공"
        );
    }

    @PostMapping
    @Operation(summary = "아이템 등록")
    public RsData<ItemCreateResponse> createItem(
            @Valid @RequestBody ItemCreateRequest request
    ) {
        Long userId = rq.getMemberId();

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
            @RequestParam(required = false) Long categoryId
    ) {
        Long userId = rq.getMemberId();
        List<Item> items;

        if (categoryId != null) {
            items = itemService.findAllByUserIdAndCategoryId(userId, categoryId);
        } else {
            items = itemService.findAllByUserIdOrderByNextReplacementDateAsc(userId);
        }

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
            @PathVariable Long itemId
    ) {
        Long userId = rq.getMemberId();

        Item item = itemService.findByIdAndUserId(itemId, userId);
        return new RsData<>("200-1", "아이템 단건 조회 성공", new ItemResponse(item));
    }

    @PutMapping("/{id}")
    @Operation(summary = "아이템 수정")
    public RsData<ItemUpdateResponse> modifyItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemUpdateRequest request
    ) {
        Long userId = rq.getMemberId();

        // 아이템 수정
        Item item = itemService.modify(userId, id, request);

        // 응답값 리턴
        return new RsData<>(
                "200",
                "아이템 수정 성공",
                new ItemUpdateResponse(item)
        );
    }

    @PutMapping("/{id}/replace")
    @Operation(summary = "아이템 교체")
    public RsData<Map<String, ItemReplaceResponse>> replaceItem(@PathVariable Long id) {
        // todo: 요청 헤더의 인증 정보를 바탕으로 user_id 받아오기
        Long userId = rq.getMemberId();

        // 아이템 교체
        Item item = itemService.replaceItem(userId, id);

        // 응답값 리턴
        return new RsData<>(
                "200",
                "아이템 교체 처리 성공",
                Map.of("item", new ItemReplaceResponse(item))
        );
    }
}

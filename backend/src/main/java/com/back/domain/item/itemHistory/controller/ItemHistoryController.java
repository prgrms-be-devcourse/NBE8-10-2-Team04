package com.back.domain.item.itemHistory.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/items/{itemId}/histories")
@RequiredArgsConstructor
@Tag(name = "ItemHistoryController", description = "아이템 이력 컨트롤러")
public class ItemHistoryController {
}

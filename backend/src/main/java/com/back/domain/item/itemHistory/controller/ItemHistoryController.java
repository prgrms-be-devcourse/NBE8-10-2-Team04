package com.back.domain.item.itemHistory.controller;

import com.back.domain.item.itemHistory.dto.ItemAllHistoryResponse;
import com.back.domain.item.itemHistory.dto.ItemHistoryResponse;
import com.back.domain.item.itemHistory.service.ItemHistoryService;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
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
    public RsData<List<ItemAllHistoryResponse>> getAllItemHistories(HttpServletRequest request) {
        System.out.println(Arrays.toString(request.getCookies()));
//        [jakarta.servlet.http.Cookie@1da37b87{apiKey=bcb68f18-845b-4f2e-90f0-2bef3b9ada1d,null}]
//        [jakarta.servlet.http.Cookie@f93a7567, jakarta.servlet.http.Cookie@e8053076, jakarta.servlet.http
//        .Cookie@6356e01f, jakarta.servlet.http.Cookie@2c2707d6]

        Long userId = rq.getMemberId();
        List<ItemAllHistoryResponse> list = itemHistoryService.getAllItemHistories(userId);

        return new RsData<>(
                "200",
                "전체 아이템 이력 조회 성공",
                list
        );
    }

    @GetMapping("/{itemId}/histories")
    @Operation(summary = "아이템 이력 조회", description = "특정 아이템의 이력을 조회합니다.")
    public List<ItemHistoryResponse> getItemHistories(@PathVariable Long itemId) {
        return itemHistoryService.getItemHistories(itemId);
    }
}

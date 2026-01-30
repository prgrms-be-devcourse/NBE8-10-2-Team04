package com.back.domain.item.item.dto;

public record MostReplacedItemResponse(
        Long itemId,
        String itemName,
        String categoryName,
        Long replacementCount,
        String imgUrl
) {
}
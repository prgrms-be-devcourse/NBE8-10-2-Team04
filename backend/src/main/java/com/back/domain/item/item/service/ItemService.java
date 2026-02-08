package com.back.domain.item.item.service;

import com.back.domain.category.category.entity.Category;
import com.back.domain.category.category.repository.CategoryRepository;
import com.back.domain.item.item.dto.CategoryAverageUsageResponse;
import com.back.domain.item.item.dto.ItemCreateRequest;
import com.back.domain.item.item.dto.ItemCycleRecommendResponse;
import com.back.domain.item.item.dto.ItemUpdateRequest;
import com.back.domain.item.item.dto.MostReplacedItemResponse;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.repository.ItemRepository;
import com.back.domain.item.item.vo.CyclePeriod;
import com.back.domain.item.itemHistory.repository.ItemHistoryRepository;
import com.back.domain.item.itemHistory.service.ItemHistoryService;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.service.UserService;
import com.back.global.exception.ServiceException;
import com.back.global.s3.S3ImageService;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final UserService userService;
    private final ItemHistoryService itemHistoryService;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final S3ImageService s3ImageService;
    private final ItemHistoryRepository itemHistoryRepository;
    private final Client genAiClient;
    private final GenerateContentConfig genAiSystemConfig;
    private final ObjectMapper objectMapper;

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    // itemId로 Item 조회 (권한 검증 없음)
    private Item findItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ServiceException(("404-1"), "존재하지 않는 아이템입니다."));
    }

    // itemId + userId로 Item 조회 및 권한 검증 (쿼리 1회로 최적화)
    private Item findOwnedItemOrThrow(Long itemId, Long userId) {
        return itemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 아이템이거나 권한이 없습니다."));
    }

    // userId로 User 조회
    private User findUserOrThrow(Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 유저입니다."));
    }

    // categoryId로 Category 조회
    private Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 카테고리입니다."));
    }

    /**
     * 이미지 URL 결정 로직 (중복 제거)
     * image 업로드된 파일
     * providedUrl 프론트에서 전달한 URL
     * existingUrl 기존 이미지 URL (수정 시)
     * return 최종 이미지 URL
     */
    private String resolveImageUrl(MultipartFile image, String providedUrl, String existingUrl) {
        // 파일이 있으면 S3 업로드
        if (image != null && !image.isEmpty()) {
            try {
                return s3ImageService.upload(image);
            } catch (IOException e) {
                throw new ServiceException("500-1", "이미지 업로드 실패: " + e.getMessage());
            }
        }

        // 프론트에서 보낸 URL
        if (StringUtils.isNotBlank(providedUrl)) {
            return providedUrl;
        }

        // 기존 URL 유지 (수정 시), 없으면 빈 문자열
        return StringUtils.isNotBlank(existingUrl) ? existingUrl : "";
    }

    // itemId로 아이템을 조회하고 요청자(userId)가 소유자인지 검증한 뒤 실제 삭제 수행
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        Item item = findOwnedItemOrThrow(itemId, userId); // 쿼리 1회로 감소
        s3ImageService.delete(item.getImgUrl()); //아이템 이미지 삭제 (추가)        
        itemRepository.delete(item);
    }

    //목록조회용
    @Transactional(readOnly = true)
    public List<Item> findAllByUserIdOrderByNextReplacementDateAsc(Long userId) {
        return itemRepository.findAllByUserIdOrderByNextReplacementDateAsc(userId);
    }

    //단건조회용
    @Transactional(readOnly = true)
    public Item findByIdAndUserId(Long itemId, Long userId) {
        return findOwnedItemOrThrow(itemId, userId); // 메서드 재사용
    }

    //카테고리별 목록조회용
    public List<Item> findAllByUserIdAndCategoryId(Long userId, Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ServiceException("404-1", "존재하지 않는 카테고리입니다.");
        }

        return itemRepository.findAllByUserIdAndCategoryId(userId, categoryId);
    }

    @Transactional(readOnly = true)
    public long count() {
        return itemRepository.count();
    }



    @Transactional
    public Item create(
            Long userId,
            Category category,
            String name,
            String imgUrl,
            LocalDate startDate,
            String cycleDays,
            LocalDate nextReplacementDate,
            Boolean isActive
    ) {
        User user = findUserOrThrow(userId);
        Item item = new Item(
                user, category, name, imgUrl,
                startDate, cycleDays, nextReplacementDate, isActive
        );

        return itemRepository.save(item);
    }

    @Transactional
    public Item createItem(Long userId, ItemCreateRequest request) {
        Category category = findCategoryOrThrow(request.categoryId()); // 메서드 재사용 + 올바른 에러 메시지

        LocalDate startDate = request.resolvedStartDate();
        CyclePeriod cyclePeriod = CyclePeriod.from(request.cycleDays());
        LocalDate nextReplacementDate = cyclePeriod.addTo(startDate);


        String finalImgUrl = resolveImageUrl(request.image(), request.imgUrl(), null); // 중복 제거

        Item item = create(
                userId,
                category,
                request.name(),
                finalImgUrl,
                startDate,
                request.cycleDays(),
                nextReplacementDate,
                true
        );

        // 아이템 생성 시 첫 번째 히스토리 생성 (추가)
        itemHistoryService.createItemHistory(item);

        return item;
    }

    public void modifyDate(Item item) {
        // 시작일 변경 : 교체를 요청한 시각
        LocalDate newStartDate = LocalDate.now();

        // 다음 교체일 변경 : 시작일 + 주기
        CyclePeriod cyclePeriod = CyclePeriod.from(item.getCycleDays());
        LocalDate newNextReplacementDate = cyclePeriod.addTo(newStartDate);

        item.modifyDate(newStartDate, newNextReplacementDate);
    }

    @Transactional
    public Item replaceItem(Long userId, Long itemId) {
        Item item = findOwnedItemOrThrow(itemId, userId); // 쿼리 1회로 감소

        // 비활성 아이템은 교체 불가
        if (!item.getIsActive()) {
            throw new ServiceException("400-2", "비활성 상태의 아이템은 교체할 수 없습니다.");
        }

        // 기존 진행중인 이력 endDate 넣기
        LocalDate today = LocalDate.now();
        itemHistoryService.endHistory(itemId, today);

        // 아이템 정보 교체
        modifyDate(item);

        // 이력 추가
        itemHistoryService.createItemHistory(item);

        return item;
    }

    @Transactional
    public Item modify(Long userId, Long itemId, ItemUpdateRequest request) {
        Item item = findOwnedItemOrThrow(itemId, userId); // 쿼리 1회로 감소
        Category category = findCategoryOrThrow(request.categoryId()); // 메서드 재사용

        String finalImgUrl = resolveImageUrl(request.image(), request.imgUrl(), item.getImgUrl()); // 중복 제거

        // 주기(cycleDays) 수정 시 다음 교체일도 함께 변경
        LocalDate nextReplacementDate = item.getNextReplacementDate();
        if (!Objects.equals(request.cycleDays(), item.getCycleDays())) {
            CyclePeriod cyclePeriod = CyclePeriod.from(request.cycleDays());
            nextReplacementDate = cyclePeriod.addTo(item.getStartDate());
        }

        // 아이템 수정
        item.modify(category, request.name(), finalImgUrl, request.cycleDays(), nextReplacementDate,
                request.isActive());

        return item;
    }

    @Transactional
    public Item toggleActive(Long userId, Long itemId) {
        Item item = findOwnedItemOrThrow(itemId, userId); // 쿼리 1회로 감소

        // 활성화 상태 토글
        item.toggleActive();

        return item;
    }

    /**
     * 특정 사용자의 카테고리별 평균 사용 기간 조회
     */
    @Transactional(readOnly = true)
    public List<CategoryAverageUsageResponse> getCategoryAverageUsage(Long userId) {
        // Repository에서 카테고리별 평균 사용 기간을 조회
        List<Map<String, Object>> rawResults = itemHistoryRepository
                .findAverageUsageDaysByCategoryForUser(userId);

        // 결과를 DTO로 변환
        return rawResults.stream()
                .map(result -> new CategoryAverageUsageResponse(
                        ((Number) result.get("categoryId")).longValue(),
                        (String) result.get("categoryName"),
                        result.get("averageUsageDays") != null
                                ? ((Number) result.get("averageUsageDays")).doubleValue()
                                : 0.0
                ))
                .toList();
    }

    public ItemCycleRecommendResponse getItemCycleRecommend(String name) {
        try {
            return CompletableFuture.supplyAsync(() ->
                            genAiClient.models.generateContent(
                                    "gemini-2.5-flash-lite",
                                    name + "의 권장 교체 주기를 알려줘.",
                                    genAiSystemConfig)
                    )
                    .orTimeout(10, TimeUnit.SECONDS) // 타임아웃 설정
                    .thenApply(response -> parseJson(response.text()))
                    .join(); // 최종 결과 대기 및 반환
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            // parseJson에서 발생한 ServiceException인 경우 그대로 다시 던짐
            if (cause instanceof ServiceException se) {
                throw se;
            }
            if (cause instanceof TimeoutException) {
                throw new ServiceException("500", "Timeout 발생");
            }
            throw new ServiceException("500", "GenAI 오류 발생");
        }
    }

    private ItemCycleRecommendResponse parseJson(String rawText) {
        // response 자체가 null인 경우 체크
        if (rawText == null || rawText.isBlank()) {
            throw new ServiceException("500", "AI로부터 응답을 받지 못했습니다.");
        }

        // Not Found 응답 처리
        if (rawText.contains("Not Found")) {
            throw new ServiceException("404", "권장 주기를 찾을 수 없는 소모품입니다.");
        }

        try {
            // {} 블록만 추출
            int start = rawText.indexOf("{");
            int end = rawText.lastIndexOf("}");

            if (start == -1 || end == -1 || start >= end) {
                throw new ServiceException("500", "AI 응답이 유효한 JSON 형식이 아닙니다.");
            }

            String cleanedJson = rawText.substring(start, end + 1);

            // JSON을 객체로 변환
            return objectMapper.readValue(cleanedJson, ItemCycleRecommendResponse.class);

        } catch (Exception e) {
            throw new ServiceException("500", "JSON 파싱 중 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 사용자의 가장 자주 교체한 아이템 순위 조회
     */
    @Transactional(readOnly = true)
    public List<MostReplacedItemResponse> getMostReplacedItems(Long userId, int limit) {
        // Repository에서 가장 자주 교체한 아이템 순위를 조회
        List<Map<String, Object>> rawResults = itemHistoryRepository
                .findMostReplacedItemsByUser(userId, limit);

        // 결과를 DTO로 변환
        return rawResults.stream()
                .map(result -> new MostReplacedItemResponse(
                        ((Number) result.get("itemId")).longValue(),
                        (String) result.get("itemName"),
                        (String) result.get("categoryName"),
                        ((Number) result.get("replacementCount")).longValue(),
                        (String) result.get("imgUrl")
                ))
                .toList();
    }
}

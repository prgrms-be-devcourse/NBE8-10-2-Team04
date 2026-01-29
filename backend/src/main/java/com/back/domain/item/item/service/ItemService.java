package com.back.domain.item.item.service;

import com.back.domain.category.category.entity.Category;
import com.back.domain.category.category.repository.CategoryRepository;
import com.back.domain.item.item.dto.ItemCreateRequest;
import com.back.domain.item.item.dto.ItemUpdateRequest;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.repository.ItemRepository;
import com.back.domain.item.item.vo.CyclePeriod;
import com.back.domain.item.itemHistory.service.ItemHistoryService;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.service.UserService;
import com.back.global.exception.ServiceException;
import com.back.global.s3.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final UserService userService;
    private final ItemHistoryService itemHistoryService;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final S3ImageService s3ImageService;

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    // itemId로 아이템을 조회하고 요청자(userId)가 소유자인지 검증한 뒤 실제 삭제 수행
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        Item item = findItemOrThrow(itemId);
        item.validateOwner(userId);
        itemRepository.delete(item);
    }

    // itemId로 Item 조회
    private Item findItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ServiceException(("400-1"), "존재하지 않는 아이템입니다."));
    }

    //목록조회용
    @Transactional(readOnly = true)
    public List<Item> findAllByUserIdOrderByNextReplacementDateAsc(Long userId) {
        return itemRepository.findAllByUserIdOrderByNextReplacementDateAsc(userId);
    }

    //단건조회용
    @Transactional(readOnly = true)
    public Item findByIdAndUserId(Long itemId, Long userId) {
        return itemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 아이템입니다."));
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

    // userId로 User 조회
    private User findUserOrThrow(Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 유저입니다."));
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
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ServiceException("400-1", "cycleDays는 필수 값입니다."));

        LocalDate startDate = request.resolvedStartDate();
        CyclePeriod cyclePeriod = CyclePeriod.from(request.cycleDays());
        LocalDate nextReplacementDate = cyclePeriod.addTo(startDate);


        String finalImgUrl = request.imgUrl(); // 기본값: 입력받은 URL 문자열

        // 파일이 실제로 들어왔는지 확인
        if (request.image() != null && !request.image().isEmpty()) {
            // 파일이 있다면 S3 업로드 후 반환된 URL을 사용
            try {
                finalImgUrl = s3ImageService.upload(request.image());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

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
        // 아이템 가져오기
        Item item = findItemOrThrow(itemId);

        // 아이템 생성자가 아니면 예외 처리(인가)
        item.validateOwner(userId);

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
        // 아이템 가져오기
        Item item = findItemOrThrow(itemId);

        // 아이템 생성자가 아니면 예외 처리(인가)
        item.validateOwner(userId);

        // 카테고리 존재 여부 확인
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 카테고리입니다."));

        String finalImgUrl = request.imgUrl(); // 1순위: 프론트에서 보낸 URL 문자열

        // 만약 파일이 넘어왔다면 S3에 업로드하고 URL 덮어쓰기
        if (request.image() != null && !request.image().isEmpty()) {
            try {
                finalImgUrl = s3ImageService.upload(request.image());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // 파일도 없고 URL도 없으면 기존 이미지 유지 (선택사항, 필요 없으면 제거 가능)
        else if (finalImgUrl == null || finalImgUrl.isBlank()) {
            finalImgUrl = "";
        }

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
        Item item = findItemOrThrow(itemId);

        // 아이템 생성자가 아니면 예외 처리(인가)
        item.validateOwner(userId);

        // 활성화 상태 토글
        item.toggleActive();

        return item;
    }
}

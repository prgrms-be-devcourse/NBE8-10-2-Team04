package com.back.domain.item.item.repository;

import com.back.domain.item.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    //목록조회
    List<Item> findAllByUserIdOrderByNextReplacementDateAsc(Long userId);

    //단건조회
    Optional<Item> findByIdAndUserId(Long id, Long userId);

    //카테고리별목록조회
    List<Item> findAllByUserIdAndCategoryId(Long userId, Long categoryId);

    // 특정 날짜가 교체 예정일이고 활성화된 아이템 조회
    List<Item> findAllByNextReplacementDateAndIsActive(LocalDate nextReplacementDate, Boolean isActive);
}

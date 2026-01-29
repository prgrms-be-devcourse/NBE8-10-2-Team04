package com.back.domain.item.itemHistory.repository;

import com.back.domain.item.itemHistory.entity.ItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ItemHistoryRepository extends JpaRepository<ItemHistory, Long> {
    List<ItemHistory> findByItemIdOrderByStartDateDesc(Long itemId);

    @Query("""
            SELECT ih
            FROM ItemHistory ih
            JOIN FETCH ih.item i
            JOIN FETCH i.category c
            WHERE i.user.id = :userId
            ORDER BY ih.startDate DESC
            """)
    List<ItemHistory> findByUserIdOrderByStartDateDesc(Long userId);

    Optional<ItemHistory> findTopByItemIdAndEndDateIsNullOrderByStartDateDesc(Long itemId);

    // 특정 사용자의 카테고리별 평균 사용 기간 조회
    @Query("""
            SELECT ih.item.category.id as categoryId,
                   ih.item.category.name as categoryName,
                   AVG(TIMESTAMPDIFF(DAY, ih.startDate, ih.endDate)) as averageUsageDays
            FROM ItemHistory ih
            WHERE ih.item.user.id = :userId
              AND ih.endDate IS NOT NULL
            GROUP BY ih.item.category.id, ih.item.category.name
            ORDER BY ih.item.category.name
            """)
    List<Map<String, Object>> findAverageUsageDaysByCategoryForUser(Long userId);
}

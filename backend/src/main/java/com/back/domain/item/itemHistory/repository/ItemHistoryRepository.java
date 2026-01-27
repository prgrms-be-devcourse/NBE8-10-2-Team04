package com.back.domain.item.itemHistory.repository;

import com.back.domain.item.itemHistory.entity.ItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemHistoryRepository extends JpaRepository<ItemHistory, Long> {
    List<ItemHistory> findByItemIdOrderByStartDateDesc(Long itemId);

    @Query("""
            SELECT ih
            FROM ItemHistory ih
            JOIN FETCH ih.item i
            WHERE i.user.id = :userId
            ORDER BY ih.startDate DESC
            """)
    List<ItemHistory> findByUserIdOrderByStartDateDesc(Long userId);

    Optional<ItemHistory> findTopByItemIdAndEndDateIsNullOrderByStartDateDesc(Long itemId);
}

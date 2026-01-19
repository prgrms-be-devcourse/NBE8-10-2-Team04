package com.back.domain.item.itemHistory.repository;

import com.back.domain.item.itemHistory.entity.ItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemHistoryRepository extends JpaRepository<ItemHistory, Long> {
}

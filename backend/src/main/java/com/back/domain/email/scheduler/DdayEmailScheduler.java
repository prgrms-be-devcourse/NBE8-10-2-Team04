package com.back.domain.email.scheduler;

import com.back.domain.email.service.EmailService;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.repository.ItemRepository;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DdayEmailScheduler {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * 매일 오전 9시에 D-Day가 0인 아이템들을 확인하고 이메일 발송
     * cron: 초 분 시 일 월 요일
     * "0 0 9 * * *" = 매일 오전 9시 정각
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional // Lazy 로딩된 연관 엔티티(User) 접근을 위해 트랜잭션 유지
    public void checkAndSendDdayEmails() {
        // 오늘 날짜
        LocalDate today = LocalDate.now();

        // 교체 예정일이 오늘이고 활성화된 아이템 목록 조회
        List<Item> itemsDueToday = itemRepository.findAllByNextReplacementDateAndIsActive(today, true);

        for (Item item : itemsDueToday) {
            User member = item.getUser(); // // 아이템과 연관된 사용자 조회
            emailService.sendDDayNotification(member.getEmail(), item); // // D-Day 알림 이메일 발송
        }
    }
}
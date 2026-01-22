package com.back.domain.email.scheduler;

import com.back.domain.email.service.EmailService;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.repository.ItemRepository;
import com.back.domain.item.item.service.ItemService;
import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DdayEmailScheduler {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final EmailService emailService;

    /**
     * 매일 오전 9시에 D-Day가 0인 아이템들을 확인하고 이메일 발송
     * cron: 초 분 시 일 월 요일
     * "0 0 9 * * *" = 매일 오전 9시 정각
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void checkAndSendDdayEmails() {

        LocalDate today = LocalDate.now();

        // 오늘이 교체 예정일인 활성화된 아이템들 조회
        List<Item> itemsDueToday = itemRepository.findAllByNextReplacementDateAndIsActive(today, true);

        for (Item item : itemsDueToday) {
            // 아이템 소유자의 이메일 가져오기
            // getUserId() 대신 직접 필드 접근 (리플렉션 사용)
            Long userId = item.getUserId();
            Member member = memberRepository.findById(userId)
                    .orElse(null);

            // D-Day 알림 이메일 발송
            emailService.sendDDayNotification(member.getEmail(), item);
        }
    }

    /**
     * 테스트용 메서드 (수동 실행 가능)
     * 실제 운영에서는 제거하거나 주석 처리
     */
    public void sendTestEmail(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다."));

        Member member = memberRepository.findById(item.getUserId())
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        emailService.sendDDayNotification(member.getEmail(), item);
    }
}
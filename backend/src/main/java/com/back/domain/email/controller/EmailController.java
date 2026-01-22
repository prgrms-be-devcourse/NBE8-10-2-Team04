package com.back.domain.email.controller;

import com.back.domain.email.scheduler.DdayEmailScheduler;
import com.back.domain.email.service.EmailService;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.repository.ItemRepository;
import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final DdayEmailScheduler ddayEmailScheduler;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    //테스트: 발송된 이메일 주소를 응답으로 반환
    @PostMapping("/test/dday/{itemId}")
    public ResponseEntity<Map<String, Object>> sendTestDdayEmail(@PathVariable Long itemId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 아이템 조회
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다."));

            // 회원 조회
            Member member = memberRepository.findById(item.getUserId())
                    .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

            // 이메일 발송 및 수신자 이메일 받기
            String sentToEmail = emailService.sendDDayNotification(member.getEmail(), item);

            // 응답 데이터 구성
            response.put("success", true);
            response.put("message", "테스트 D-Day 이메일 발송 성공");
            response.put("recipientEmail", sentToEmail);  // 발송된 이메일 주소
            response.put("itemId", itemId);
            response.put("itemName", item.getName());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "이메일 발송 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
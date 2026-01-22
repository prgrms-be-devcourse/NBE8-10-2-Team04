package com.back.domain.email.controller;

import com.back.domain.email.service.EmailService;
import com.back.domain.item.item.entity.Item;
import com.back.domain.item.item.repository.ItemRepository;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.repository.UserRepository;
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
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    //테스트용 D-Day 이메일 발송 API
    @PostMapping("/test/dday/{itemId}")
    public ResponseEntity<Map<String, Object>> sendTestDdayEmail(@PathVariable Long itemId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 아이템 조회
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다."));

            // 아이템에 연결된 사용자 조회
            User user = item.getUser();

            // 사용자 미연결 방어
            if (user == null) {
                throw new RuntimeException("아이템에 연결된 사용자가 없습니다.");
            }

            // 이메일 미등록 사용자 방어
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                throw new RuntimeException("사용자의 이메일 주소가 없습니다.");
            }

            // D-Day 알림 이메일 발송
            String sentToEmail = emailService.sendDDayNotification(user.getEmail(), item);

            // 테스트 확인용 응답 데이터
            response.put("success", true);
            response.put("message", "테스트 D-Day 이메일 발송 성공");
            response.put("recipientEmail", sentToEmail);
            response.put("itemId", itemId);
            response.put("itemName", item.getName());
            response.put("userId", user.getId());
            response.put("userLoginId", user.getLoginId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 예외 발생시 스택 트레이스 출력
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "이메일 발송 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
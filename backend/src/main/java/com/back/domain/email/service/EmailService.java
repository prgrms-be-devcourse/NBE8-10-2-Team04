package com.back.domain.email.service;

import com.back.domain.item.item.entity.Item;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    // 기존 메서드
    public void sendMimeMessage() {
        // ... 기존 코드 유지
    }

    // 수정된 메서드: 발송된 이메일 주소 반환
    public String sendDDayNotification(String recipientEmail, Item item) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // 아이템 소유자의 이메일로 발송
            mimeMessageHelper.setTo(recipientEmail);

            // 메일 제목
            mimeMessageHelper.setSubject("[교체 알림] " + item.getName() + " 교체 시기입니다!");

            // HTML 형식의 이메일 내용
            String content = String.format("""
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <style>
                                    body { font-family: Arial, sans-serif; }
                                    .container { margin: 50px; padding: 20px; }
                                    .header { background-color: #4CAF50; color: white; padding: 15px; border-radius: 5px; }
                                    .content { margin-top: 20px; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }
                                    .item-info { background-color: #f9f9f9; padding: 15px; margin: 10px 0; border-radius: 5px; }
                                    .footer { margin-top: 20px; color: #666; font-size: 12px; }
                                </style>
                            </head>
                            <body>
                                <div class="container">
                                    <div class="header">
                                        <h2>🔔 교체 알림</h2>
                                    </div>
                            
                                    <div class="content">
                                        <p>안녕하세요!</p>
                                        <p>등록하신 아이템의 교체 시기가 되었습니다.</p>
                            
                                        <div class="item-info">
                                            <h3>📦 아이템 정보</h3>
                                            <p><strong>아이템 이름:</strong> %s</p>
                                            <p><strong>시작일:</strong> %s</p>
                                            <p><strong>교체 주기:</strong> %s</p>
                                            <p><strong>교체 예정일:</strong> %s</p>
                                            <p><strong>D-Day:</strong> <span style="color: red; font-weight: bold;">오늘!</span></p>
                                        </div>
                            
                                        <p>아이템을 교체하신 후 앱에서 교체 완료 처리를 해주세요.</p>
                                    </div>
                            
                                    <div class="footer">
                                        <p>본 메일은 자동으로 발송되었습니다.</p>
                                    </div>
                                </div>
                            </body>
                            </html>
                            """,
                    item.getName(),
                    item.getStartDate(),
                    item.getCycleDays(),
                    item.getNextReplacementDate()
            );

            mimeMessageHelper.setText(content, true);
            javaMailSender.send(mimeMessage);

            // 발송 성공 시 이메일 주소 반환
            return recipientEmail;
        } catch (Exception e) {
            throw new RuntimeException("메일 발송 실패", e);
        }
    }
}
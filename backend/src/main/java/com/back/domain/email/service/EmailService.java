package com.back.domain.email.service;

import com.back.domain.email.dto.ReplacementEmailContent;
import com.back.domain.item.item.entity.Item;
import com.back.global.exception.ServiceException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;


    //  발송된 이메일 주소 반환
    public String sendDDayNotification(String recipientEmail, Item item) {
        ReplacementEmailContent emailContent = ReplacementEmailContent.from(item);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // 아이템 소유자의 이메일로 발송
            mimeMessageHelper.setTo(recipientEmail);

            // 메일 제목
            mimeMessageHelper.setSubject("[교체 알림] " + item.getName() + " 교체 시기입니다!");

            // HTML 형식의 이메일 내용
            mimeMessageHelper.setText(emailContent.toHtmlContent(), true);

            javaMailSender.send(mimeMessage);

            // 발송 성공 시 이메일 주소 반환
            return recipientEmail;
        } catch (Exception e) {
            throw new ServiceException("500-1", "메일 발송 실패: " + e.getMessage());
        }
    }
}

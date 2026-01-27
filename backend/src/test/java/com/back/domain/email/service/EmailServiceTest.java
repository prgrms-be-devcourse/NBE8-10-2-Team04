package com.back.domain.email.service;

import com.back.domain.category.category.entity.Category;
import com.back.domain.item.item.entity.Item;
import com.back.domain.user.user.entity.User;
import com.back.global.exception.ServiceException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 확장 사용 (Mock 객체 초기화)
@DisplayName("EmailService 테스트")
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender; // 실제 메일 전송을 막기 위한 Mock 객체

    @InjectMocks
    private EmailService emailService; // Mock이 주입된 EmailService

    private User testUser;
    private Category testCategory;
    private Item testItem;
    private String recipientEmail;

    @BeforeEach
    void setUp() {
        // 테스트 user
        testUser = new User(
                "testUser",
                "password123",
                "test@example.com"
        );

        // 테스트 Category
        testCategory = new Category(
                "테스트 카테고리"
        );

        // D-Day 알림 테스트 Item 생성
        testItem = new Item(
                testUser,
                testCategory,
                "칫솔",
                "/images/toothbrush.png",
                LocalDate.of(2024, 1, 1),
                "90일",
                LocalDate.of(2024, 4, 1),
                true
        );

        // 기본 수신자 이메일
        recipientEmail = "test@example.com";
    }

    @Test
    @DisplayName("이메일 발송 성공 테스트")
    void sendDDayNotification_Success() {
        // JavaMailSender가 MimeMessage를 정상 생성하도록 설정
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        //  메일 전송 시 예외 없이 정상 동작하도록 설정
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // D-Day 알림 메일 발송
        String result = emailService.sendDDayNotification(recipientEmail, testItem);

        // 반환값이 수신자 이메일인지 검증
        assertThat(result).isEqualTo(recipientEmail);

        // 메일 생성 및 전송이 각각 1번씩 호출되었는지 검증
        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("이메일 발송 실패 시 예외 발생 테스트")
    void sendDDayNotification_Failure() {
        // MimeMessage 생성은 정상
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // 메일 전송 시 RuntimeException 발생하도록 설정
        doThrow(new RuntimeException("메일 서버 오류"))
                .when(javaMailSender).send(any(MimeMessage.class));

        // 메일 발송 실패 시 ServiceException이 발생하는지 검증
        assertThatThrownBy(() -> emailService.sendDDayNotification(recipientEmail, testItem))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("메일 발송 실패");

        // 메일 생성 및 전송 시도가 있었는지 검증
        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("다양한 아이템 정보로 이메일 발송 테스트")
    void sendDDayNotification_WithDifferentItems() {
        // 다른 아이템 정보로 테스트
        Item differentItem = new Item(
                testUser,
                testCategory,
                "수세미",
                "/images/sponge.png",
                LocalDate.of(2024, 6, 1),
                "30일",
                LocalDate.of(2024, 7, 1),
                true
        );

        // 메일 생성 및 전송 정상 설정
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // 다른 수신자 + 다른 아이템으로 메일 발송
        String result = emailService.sendDDayNotification("another@example.com", differentItem);

        // 반환값 검증
        assertThat(result).isEqualTo("another@example.com");

        // 메일 생성 및 전송이 1회 호출되었는지 검증
        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("여러 수신자에게 이메일 발송 테스트")
    void sendDDayNotification_MultipleRecipients() {
        // 여러 수신자 목록
        String[] emails = {
                "user1@example.com",
                "user2@example.com",
                "user3@example.com"
        };

        // 메일 생성 및 전송 정상 설정
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // 각 수신자에게 메일 발송 및 반환값 검증
        for (String email : emails) {
            String result = emailService.sendDDayNotification(email, testItem);
            assertThat(result).isEqualTo(email);
        }

        // 수신자 수만큼 메일 생성/전송이 호출되었는지 검증
        verify(javaMailSender, times(3)).createMimeMessage();
        verify(javaMailSender, times(3)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("한 명의 수신자에게 여러 아이템에 대한 이메일 발송 테스트")
    void sendDDayNotification_SingleRecipientMultipleItems() {
        Item item1 = new Item(
                testUser,
                testCategory,
                "칫솔",
                "/images/toothbrush.png",
                LocalDate.of(2024, 1, 1),
                "90일",
                LocalDate.of(2024, 4, 1),
                true
        );

        Item item2 = new Item(
                testUser,
                testCategory,
                "수세미",
                "/images/sponge.png",
                LocalDate.of(2024, 2, 1),
                "30일",
                LocalDate.of(2024, 3, 1),
                true
        );

        Item item3 = new Item(
                testUser,
                testCategory,
                "샤워타올",
                "/images/towel.png",
                LocalDate.of(2024, 3, 1),
                "180일",
                LocalDate.of(2024, 9, 1),
                true
        );

        // 교체 알림이 필요한 아이템 리스트
        Item[] items = {item1, item2, item3};

        // 메일 생성 및 전송 정상 설정
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // 동일한 수신자에게 여러 아이템에 대한 메일 발송
        for (Item item : items) {
            String result = emailService.sendDDayNotification(recipientEmail, item);

            // 각 메일 발송마다 동일한 수신자 이메일이 반환되는지 검증
            assertThat(result).isEqualTo(recipientEmail);
        }

        // 아이템 개수만큼 메일 생성/전송이 호출되었는지 검증
        verify(javaMailSender, times(3)).createMimeMessage();
        verify(javaMailSender, times(3)).send(any(MimeMessage.class));
    }
}

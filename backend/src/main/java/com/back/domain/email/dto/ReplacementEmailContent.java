package com.back.domain.email.dto;

import com.back.domain.item.item.entity.Item;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReplacementEmailContent {
    private String itemName;
    private LocalDate startDate;
    private String cycleDays;
    private LocalDate nextReplacementDate;

    public static ReplacementEmailContent from(Item item) {
        return ReplacementEmailContent.builder()
                .itemName(item.getName())
                .startDate(item.getStartDate())
                .cycleDays(item.getCycleDays())
                .nextReplacementDate(item.getNextReplacementDate())
                .build();
    }

    public String toHtmlContent() {
        return String.format("""
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
                itemName,
                startDate,
                cycleDays,
                nextReplacementDate
        );
    }

    public String getEmailSubject() {
        return "[교체 알림] " + itemName + " 교체 시기입니다!";
    }
}
package com.back.global.genAi;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Part;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenAiConfig {
    @Value("${google.gemini.api-key}")
    private String geminiApiKey;

    @Bean
    public Client genAiClient() {
        return Client.builder()
                .apiKey(geminiApiKey)
                .build();
    }

    @Bean
    public GenerateContentConfig genAiSystemConfig() {
        // 생성형 AI 모델 기본 설정
        String systemPrompt = "너는 살림 전문가야. 사용자가 소모품 이름을 말하면 권장 교체 주기를 알려줘야 해." +
                "응답은 반드시 다른 설명 없이 다음 JSON 형식으로만 보내줘: " +
                "{\"cycleValue\": 자연수, \"cycleUnit\": \"d(일)/m(개월)/y(년) 중 하나\"}" +
                "일반적인 소모품이 아니라면 Not Found로 응답해줘.";

        return GenerateContentConfig.builder()
                .systemInstruction(Content.fromParts(Part.fromText(systemPrompt)))
                .temperature(0.2f) // 창의성을 낮추어 JSON 형식을 더 잘 지키게 설정
                .build();
    }
}

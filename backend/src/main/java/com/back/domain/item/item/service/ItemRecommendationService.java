package com.back.domain.item.item.service;

import com.back.domain.item.item.dto.ItemCycleRecommendResponse;
import com.back.global.exception.ErrorCode;
import com.back.global.exception.ServiceException;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * AI를 활용한 아이템 추천 서비스
 * - 아이템 교체 주기 추천
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRecommendationService {
    private final Client genAiClient;
    private final GenerateContentConfig genAiSystemConfig;
    private final ObjectMapper objectMapper;

    // AI 타임아웃 설정 (10초)
    private static final long AI_TIMEOUT_SECONDS = 10;

    // Gemini 모델명
    private static final String GEMINI_MODEL = "gemini-2.5-flash-lite";

    /**
     * AI를 통해 아이템의 권장 교체 주기를 추천받습니다.
     *
     * @param itemName 아이템 이름
     * @return 권장 교체 주기 정보
     * @throws ServiceException AI 응답 실패, 타임아웃, JSON 파싱 실패 시
     */
    public ItemCycleRecommendResponse getItemCycleRecommend(String itemName) {
        try {
            return CompletableFuture.supplyAsync(() ->
                            genAiClient.models.generateContent(
                                    GEMINI_MODEL,
                                    buildPrompt(itemName),
                                    genAiSystemConfig)
                    )
                    .orTimeout(AI_TIMEOUT_SECONDS, TimeUnit.SECONDS) // 타임아웃 설정
                    .thenApply(response -> parseJson(response.text()))
                    .join(); // 최종 결과 대기 및 반환
        } catch (CompletionException e) {
            return handleCompletionException(e);
        }
    }

    /**
     * AI 요청을 위한 프롬프트 생성
     */
    private String buildPrompt(String itemName) {
        return itemName + "의 권장 교체 주기를 알려줘.";
    }

    /**
     * CompletionException 처리
     */
    private ItemCycleRecommendResponse handleCompletionException(CompletionException e) {
        Throwable cause = e.getCause();

        // parseJson에서 발생한 ServiceException인 경우 그대로 다시 던짐
        if (cause instanceof ServiceException se) {
            throw se;
        }

        // 타임아웃 예외 처리
        if (cause instanceof TimeoutException) {
            log.error("AI 응답 타임아웃: {}초 초과", AI_TIMEOUT_SECONDS);
            throw new ServiceException(ErrorCode.AI_TIMEOUT);
        }

        // 기타 예외 처리
        log.error("AI 처리 중 예상치 못한 오류 발생", cause);
        throw new ServiceException(ErrorCode.AI_ERROR);
    }

    /**
     * AI 응답 JSON 파싱
     *
     * @param rawText AI로부터 받은 원본 텍스트
     * @return 파싱된 ItemCycleRecommendResponse
     * @throws ServiceException 파싱 실패 시
     */
    private ItemCycleRecommendResponse parseJson(String rawText) {
        // response 자체가 null인 경우 체크
        if (rawText == null || rawText.isBlank()) {
            log.error("AI로부터 빈 응답을 받음");
            throw new ServiceException(ErrorCode.AI_NO_RESPONSE);
        }

        // Not Found 응답 처리
        if (rawText.contains("Not Found")) {
            log.warn("AI가 해당 아이템의 권장 주기를 찾을 수 없음: {}", rawText);
            throw new ServiceException(ErrorCode.AI_ITEM_NOT_FOUND);
        }

        try {
            String cleanedJson = extractJsonBlock(rawText);

            // JSON을 객체로 변환
            return objectMapper.readValue(cleanedJson, ItemCycleRecommendResponse.class);
        } catch (Exception e) {
            log.error("AI 응답 JSON 파싱 실패. 원본 텍스트: {}", rawText, e);
            throw new ServiceException(ErrorCode.JSON_PARSING_ERROR);
        }
    }

    /**
     * 원본 텍스트에서 JSON 블록만 추출
     *
     * @param rawText 원본 텍스트
     * @return 추출된 JSON 문자열
     * @throws ServiceException JSON 블록을 찾을 수 없을 때
     */
    private String extractJsonBlock(String rawText) {
        // {} 블록만 추출
        int start = rawText.indexOf("{");
        int end = rawText.lastIndexOf("}");

        if (start == -1 || end == -1 || start >= end) {
            log.error("AI 응답에서 유효한 JSON 블록을 찾을 수 없음: {}", rawText);
            throw new ServiceException(ErrorCode.AI_INVALID_JSON);
        }

        return rawText.substring(start, end + 1);
    }
}

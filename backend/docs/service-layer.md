# 🏗️ 서비스 계층 아키텍처 가이드

본 프로젝트는 **단일 책임 원칙(SRP)**을 준수하여, 하나의 서비스 클래스가 너무 많은 책임을 갖지 않도록 기능을 분리합니다.

## 1. 서비스 분리 원칙

비즈니스 로직이 비대해지거나 성격이 다른 기능이 혼재될 경우, 아래 기준에 따라 별도의 서비스 클래스로 추출(Extract Class)합니다.

| 서비스 종류 | 역할 및 책임 | 명명 규칙 | 예시 |
| --- | --- | --- | --- |
| **Core Service** | 도메인의 핵심 **CRUD** 및 상태 변경 로직 | `[Domain]Service` | `ItemService` |
| **Statistics Service** | 복잡한 **통계/집계** 조회 (Read-Only 권장) | `[Domain]StatisticsService` | `ItemStatisticsService` |
| **External Service** | **외부 API** (AI, 결제, 메일) 연동 및 예외 처리 | `[Domain][Feature]Service` | `ItemRecommendationService` |

## 2. 모범 사례 (Best Practices)

### 2.1 핵심 비즈니스 로직 (Core)

* 가장 빈번하게 사용되는 조회, 생성, 수정, 삭제 로직을 담당합니다.
* 트랜잭션(`@Transactional`) 관리가 필수적입니다.
* **예시:** `ItemService` (아이템 생성, 교체, 활성화 토글)

### 2.2 외부 시스템 연동 (External/AI)

* 실행 시간이 길거나 실패 가능성이 높은 로직(AI, S3, 외부 API)은 핵심 로직과 분리합니다.
* **타임아웃(Timeout)** 설정과 **별도의 예외 처리(Try-Catch)**가 필수입니다.
* **예시:** `ItemRecommendationService` (Gemini AI 연동)
```java
// 외부 서비스는 핵심 로직(ItemService)과 분리하여 독립적으로 실패를 제어함
public Recommendation getRecommendation(...) {
    try {
        // 외부 API 호출
    } catch (TimeoutException e) {
        // 타임아웃 처리
    }
}
```



### 2.3 통계 및 조회 전용 (Statistics)

* 단순 CRUD가 아닌 복잡한 `Group By`, `Join` 등이 포함된 조회 로직을 담당합니다.
* 데이터 변경이 없으므로 `@Transactional(readOnly = true)`를 기본으로 사용합니다.
* **예시:** `ItemStatisticsService` (카테고리별 평균 사용일, 교체 순위 조회)

## 3. 컨트롤러에서의 사용 (Facade Pattern)

컨트롤러(`Controller`)는 필요한 여러 서비스를 주입받아 조합(Composition)하여 사용합니다.

```java
@RestController
@RequiredArgsConstructor
public class ItemController {
    // 성격에 따라 분리된 서비스들을 각각 주입받음
    private final ItemService itemService;                  // 1. CRUD
    private final ItemStatisticsService itemStatisticsService; // 2. 통계
    private final ItemRecommendationService itemRecommendationService; // 3. AI
    
    // ...
}
```

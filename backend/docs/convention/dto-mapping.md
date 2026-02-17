#  DTO 매핑 및 유틸리티 사용 가이드

본 프로젝트는 코드의 응집도를 높이고 유지보수성을 강화하기 위해 **DTO 정적 팩토리 메서드 패턴**과 **공통 유틸리티(D-Day 계산)** 사용을 표준으로 정의

## 1. DTO 매핑 전략 (Static Factory Method)

### 1.1 개요

기존에는 컨트롤러나 서비스 계층에서 DTO의 생성자(`new Dto(...)`)를 직접 호출하여 엔티티를 변환. 이는 변환 로직이 여러 곳에 흩어지게 하고, 엔티티 필드 변경 시 수정 범위를 넓히는 문제 발생.

이를 해결하기 위해 **DTO 내부에서 엔티티 변환 로직을 캡슐화**하는 정적 팩토리 메서드 패턴을 도입.

### 1.2 네이밍 규칙 (Naming Convention)

| 메서드명 | 설명 | 사용 예시 |
| --- | --- | --- |
| **`from`** | 하나의 인자(주로 Entity)를 받아 DTO로 변환 | `UserDto.from(user)` |
| **`of`** | 여러 인자(Entity + 추가 정보)를 받아 DTO로 변환 | `CategoryResponse.of(category, count)` |
| **`fromList`** | Entity 리스트를 받아 DTO 리스트로 변환 | `ItemResponse.fromList(items)` |

### 1.3 구현 예시 (Code Example)

**변경 전 (AS-IS): 생성자 직접 호출**

```java
// Controller
return new ItemResponse(
    item.getId(),
    item.getName(),
    // ... 필드 10개 나열 ...
);
```

**변경 후 (TO-BE): 정적 팩토리 메서드 사용**

**DTO 클래스 (`ItemResponse.java`)**

```java
public record ItemResponse(
    Long id,
    String name,
    // ...
) {
    // 1. 단일 변환
    public static ItemResponse from(Item item) {
        return new ItemResponse(
            item.getId(),
            item.getName(),
            // 변환 로직이 이곳에 응집됨
        );
    }

    // 2. 리스트 변환
    public static List<ItemResponse> fromList(List<Item> items) {
        return items.stream()
                .map(ItemResponse::from)
                .toList();
    }
}
```

**Controller 클래스**

```java
@GetMapping("/{itemId}")
public RsData<ItemResponse> getItem(@PathVariable Long itemId) {
    Item item = itemService.findById(itemId);
    
    // 호출이 매우 간결해짐
    return new RsData<>("200", "조회 성공", ItemResponse.from(item)); 
}
```

---

## 2. 날짜 계산 유틸리티 (`DDayCalculator`)

### 2.1 개요

아이템의 교체 주기 관리 등에서 `D-Day` 계산 로직(`ChronoUnit.DAYS.between...`)이 여러 DTO와 서비스에 중복되어 사용. 
이를 중앙에서 관리하기 위해 유틸리티 클래스로 분리.

### 2.2 사용 방법

* **경로:** `com.back.domain.item.item.util.DDayCalculator`
* **기능:** `LocalDate`를 입력받아 오늘 날짜 기준 D-Day(남은 일수)를 반환.

### 2.3 코드 예시

**DTO 내부에서 사용**

```java
import com.back.domain.item.item.util.DDayCalculator;

public static ItemResponse from(Item item) {
    return new ItemResponse(
        // ...
        // 복잡한 날짜 계산 로직을 유틸리티에 위임
        DDayCalculator.calculate(item.getNextReplacementDate()) 
    );
}
```

**참고: 유틸리티 로직**

```java
public class DDayCalculator {
    public static Long calculate(LocalDate targetDate) {
        if (targetDate == null) return -1L;
        return ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }
}
```

---

## 3. 리팩토링 기대 효과

1. **높은 응집도 (High Cohesion):** 엔티티를 DTO로 변환하는 책임이 DTO 클래스 하나에 모임.
2. **낮은 결합도 (Low Coupling):** 컨트롤러는 DTO의 내부 구조(필드 순서 등)를 알 필요 없이 `from()` 메서드만 호출.
3. **코드 중복 제거:** 날짜 계산이나 리스트 변환 로직(`stream().map...`)을 매번 작성하지 않아도 됨.
4. **가독성 향상:** 비즈니스 로직의 흐름이 명확.
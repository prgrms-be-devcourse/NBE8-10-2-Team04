# 백엔드 예외 처리 가이드 (Exception Handling Guide)
본 프로젝트는 예외 처리를 일관성 있게 관리하기 위해 `ErrorCode` Enum과 `ServiceException`을 사용합니다. 새로운 예외를 추가하거나 사용할 때 아래 가이드를 따라주세요.

## 1. 아키텍처 개요
- **ErrorCode**: 에러 코드(`String`), 메시지(`String`), HTTP 상태(`HttpStatus`)를 정의하는 Enum입니다. 
- **ServiceException**: 비즈니스 로직에서 발생하는 체크 예외(Runtime)를 감싸는 커스텀 예외 클래스입니다. 
- **GlobalExceptionHandler**: 전역에서 발생하는 예외를 잡아 표준 응답(`RsData`)으로 변환합니다.

## 2. 사용 방법
### 2.1 예외 정의하기 (`ErrorCode.java`)
새로운 에러가 필요하면 `ErrorCode` Enum에 상수를 추가합니다. 네이밍 규칙은 `도메인_상세이유` 또는 `상황설명` (Snake Case)을 권장합니다.

```Java
public enum ErrorCode {
// ... 기존 코드 ...

    // [신규 추가] 
    // 형식: 이름("에러코드", "메시지", HttpStatus)
    COUPON_ALREADY_USED("409-2", "이미 사용된 쿠폰입니다.", HttpStatus.CONFLICT),
    
    // ...
}
```
### 2.2 예외 발생시키기 (Service Layer)
비즈니스 로직에서 예외 상황이 발생하면 `ServiceException`을 던집니다.

**Case 1: 단순 에러 발생**

```Java
// 아이템을 찾을 수 없을 때
Item item = itemRepository.findById(id)
.orElseThrow(() -> new ServiceException(ErrorCode.ITEM_NOT_FOUND));
```

**Case 2: 동적 메시지 사용** 메시지에 변수(ID, 이름 등)를 포함해야 한다면 `ErrorCode` 메시지에 `%s` 포맷팅을 사용하고 인자를 전달합니다.

```Java
// ErrorCode 정의: "존재하지 않는 게시글입니다 (ID: %s)"
throw new ServiceException(ErrorCode.POST_NOT_FOUND, postId);
```

### 2.3 하위 호환성 (주의사항)
현재 리팩토링 과도기이므로 문자열을 직접 입력하는 생성자(`deprecated`)가 남아있으나, **신규 코드 작성 시에는 반드시 `ErrorCode`를 사용해야 합니다.**

```Java
// 지양해주세요 (Legacy)
throw new ServiceException("500", "알 수 없는 에러");

// 권장합니다
throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
```

### 2.4 유효성 검증 (Validation) 처리 규칙

본 프로젝트는 `@Valid` 어노테이션을 사용한 유효성 검증 시, 컨트롤러에서 `BindingResult`를 직접 핸들링하지 않습니다.

#### **규칙**

1. **`BindingResult` 파라미터 금지:** 컨트롤러 메서드 인자에 `BindingResult`를 선언하지 마세요.
2. **자동 예외 발생:** 유효성 검증 실패 시 Spring이 자동으로 `MethodArgumentNotValidException`을 발생시킵니다.
3. **전역 처리:** `GlobalExceptionHandler`가 해당 예외를 잡아 표준 에러 응답(`RsData`)으로 변환합니다.

#### **코드 예시**

**지양 (Legacy: 직접 처리 방식)**

```java
@PostMapping("/signup")
public RsData<UserDto> join(@Valid @RequestBody UserJoinRequest req, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
        return new RsData<>("400", bindingResult.getFieldError().getDefaultMessage());
    }
    // ...
}
```

**권장 (New Standard: 전역 처리 방식)**

```java
@PostMapping("/signup")
public RsData<UserDto> join(@Valid @RequestBody UserJoinRequest req) {
    // 유효성 검증 실패 시, 이곳에 도달하지 않고 GlobalExceptionHandler로 넘어갑니다.
    User user = userService.join(...);
    return new RsData<>("201", "성공", ...);
}
```
---

## 3. 응답 포맷
   클라이언트는 항상 아래와 같은 JSON 형태의 `RsData` 응답을 받게 됩니다.

```JSON
{
"resultCode": "404-1",
"msg": "존재하지 않는 아이템입니다.",
"data": null
}
```

## 4. 로깅
   `GlobalExceptionHandler`에서 `ServiceException` 발생 시 WARN 레벨로 로그가 기록됩니다. 디버깅 시 서버 로그를 확인해 주세요.
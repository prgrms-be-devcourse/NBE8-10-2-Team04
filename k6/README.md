# k6 부하 테스트 가이드

## 사전 준비

### k6 설치 (Mac)

```bash
brew install k6
```

### 백엔드 실행 필수

```bash
cd backend
./gradlew bootRun
```

### 환경 변수 설정

`k6/.env` 파일을 만들어 베이스 URL과 로그인 계정을 지정합니다. (예시는 로컬 백엔드)

```
BASE_URL=http://localhost:8080
USER_ID=your-id@example.com
USER_PW=your-password
```

## 실행 방법

프로젝트 루트에서:

```bash
set -a; source k6/.env; set +a
k6 run k6/load-test.js
```

## 테스트 시나리오 (load-test.js)

| 단계 | 시간 | 동시 사용자 수 |
|------|------|---------------|
| Ramp-up | 10초 | 0 → 50명 |
| 유지 | 20초 | 50명 |
| Ramp-up | 10초 | 50 → 100명 |
| 유지 | 20초 | 100명 |
| Ramp-down | 10초 | 100 → 0명 |

총 **70초** 동안 실행되며, 각 사용자는 매 반복마다 다음 API를 호출합니다:

- `GET /api/v1/items` - 아이템 목록 조회
- `GET /api/v1/categories` - 카테고리 목록 조회
- `GET /api/v1/items/{id}` - 목록에서 첫 번째 아이템 상세 조회(데이터가 있을 때만)
- `GET /api/v1/user/signup` - 회원가입 (permitAll)
- `GET /actuator/health` - 헬스 체크(Spring Boot Actuator, 각 VU가 10회 중 1회만 호출)

## 임계값(thresholds)

- `http_req_duration: p(95) < 2000ms` : 전체 요청의 95%가 2초 미만
- `checks{type:health}: rate > 0.95` : health 체크 성공률 95% 이상

Health 체크는 부하 집중을 피하기 위해 각 VU가 10번 반복 중 1번만 호출하도록 샘플링되어 있습니다.

## 결과 확인

- 터미널에 요청 수, 응답 시간, 실패율 등이 출력됩니다
- Grafana (`http://localhost:3030`)에서 Last 5 minutes로 설정하면 부하 테스트 중 메트릭 변화를 시각적으로 확인할 수 있습니다

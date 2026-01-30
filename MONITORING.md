# Prometheus + Grafana 모니터링 가이드

## 사전 준비

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) 설치 및 실행

## 실행 순서

### 1. Docker 컨테이너 실행 (Prometheus + Grafana + Node Exporter)

프로젝트 루트에서:

```bash
docker-compose up -d
```

### 2. 백엔드 실행

```bash
cd backend
./gradlew bootRun
```

### 3. 프론트엔드 실행

```bash
cd frontend
npm run dev
```

## 접속 URL

| 서비스 | URL | 비고 |
|--------|-----|------|
| Prometheus | http://localhost:9090 | 메트릭 수집 확인 |
| Grafana | http://localhost:3030 | 대시보드 (admin / admin) |
| Node Exporter | http://localhost:9100 | OS 메트릭 엔드포인트 |
| Actuator | http://localhost:8080/actuator/prometheus | 백엔드 메트릭 엔드포인트 |

## Grafana 대시보드 설정

1. http://localhost:3030 접속 → admin / admin 로그인
2. Prometheus Data Source는 자동 등록됨 (provisioning)
3. **Dashboards** → **New** → **Import**
4. 아래 대시보드를 각각 Import:

| 대시보드 ID | 이름 | 용도 |
|------------|------|------|
| `4701` | JVM (Micrometer) | Spring Boot 애플리케이션 메트릭 (JVM, HTTP 요청, DB 커넥션 등) |
| `1860` | Node Exporter Full | OS 레벨 메트릭 (CPU, 메모리, 디스크, 네트워크) |

5. ID 입력 → **Load** → Prometheus 선택 (있는 경우) → **Import**

## 종료

```bash
docker-compose down
```

> **주의:** `docker-compose down -v` 를 사용하면 Grafana 대시보드 설정이 초기화됩니다. 볼륨 유지를 위해 `-v` 옵션 없이 종료하세요.

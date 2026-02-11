# AWS EC2 웹 서비스 구축 및 CI/CD 배포 명세서

## 1. 프로젝트 개요

* **프로젝트:** Spring Boot (Backend) + Next.js (Frontend) + MySQL (DB)
* **인프라:** AWS EC2 (`t2.micro`, Ubuntu 24.04), 서울 리전 (`ap-northeast-2`)
* **배포 방식:** Docker Container, GitHub Actions (CI/CD)
* **서버 IP:** `43.203.2.175` (탄력적 IP)

---

## 2. 핵심 파일 명세 (Configuration Files)

### 2.1. Frontend 설정 (`frontend/Dockerfile`)

Next.js의 빌드 타임 환경 변수 주입을 위해 `ARG`와 `ENV`를 설정

```dockerfile
FROM node:20-alpine AS deps
WORKDIR /app
COPY package*.json ./
RUN npm ci

FROM node:20-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
# GitHub Actions에서 빌드 시점에 서버 IP 주입
ARG NEXT_PUBLIC_API_BASE_URL
ENV NEXT_PUBLIC_API_BASE_URL=$NEXT_PUBLIC_API_BASE_URL
RUN npm run build

FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
EXPOSE 3000
CMD ["node", "server.js"]

```

### 2.2. Backend 설정 (`backend/Dockerfile`)

빌드 환경(JDK)과 실행 환경(JRE)을 분리하여 이미지를 최적화

```dockerfile
# 1. Builder Stage
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY gradlew ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# 2. Runner Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

```

### 2.3. 서버 배포 설정 (`docker-compose.yml` on Server)

**서버의 `/home/ubuntu/my-app/docker-compose.yml` 내용**
외부 `.env` 파일을 주입하고, Frontend가 Backend의 공인 IP를 바라보게 설정

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: app-mysql
    ports: ["3306:3306"]
    environment:
      - MYSQL_ROOT_PASSWORD=password
      - MYSQL_DATABASE=appdb
    volumes:
      - mysql-data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s; timeout: 5s; retries: 5

  backend:
    image: ghcr.io/prgrms-be-devcourse/nbe8-10-2-team04/backend:latest
    container_name: app-backend
    ports: ["8080:8080"]
    env_file:
      - .env  # 서버의 .env 파일 로드
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      # .env 변수 매핑
      - AWS_ACCESS_KEY=${AWS_ACCESS_KEY}
      - AWS_SECRET_KEY=${AWS_SECRET_KEY}
      - AWS_BUCKET_NAME=${AWS_BUCKET_NAME}
      - MAIL_USERNAME=${MAIL_USERNAME}
      - MAIL_PASSWORD=${MAIL_PASSWORD}
      - JWT_SECRET_KEY=${JWT_SECRET_KEY}
      - GEMINI_API_KEY=${GEMINI_API_KEY}
    depends_on:
      mysql: { condition: service_healthy }
    restart: unless-stopped

  frontend:
    image: ghcr.io/prgrms-be-devcourse/nbe8-10-2-team04/frontend:latest
    container_name: app-frontend
    ports: ["3000:3000"]
    environment:
      # 서버 IP로 설정
      - NEXT_PUBLIC_API_URL=http://43.203.2.175:8080
    depends_on: ["backend"]
    restart: unless-stopped

volumes:
  mysql-data:

```

### 2.4. CI/CD 파이프라인 (`.github/workflows/deploy.yml`)

`ghcr.io`에 이미지를 푸시하고, 서버에서 `docker compose pull`을 실행합니다. Frontend 빌드 시 `build-args`로 서버 IP를 주입

```yaml
# ... (Build Job 생략) ...
      - name: Build and push Frontend Image
        uses: docker/build-push-action@v5
        with:
          context: ./frontend
          push: true
          tags: ghcr.io/prgrms-be-devcourse/nbe8-10-2-team04/frontend:latest
          build-args: |
            # 빌드 시점에 서버 IP 주입
            NEXT_PUBLIC_API_BASE_URL=http://43.203.2.175:8080

  deploy:
    needs: build-and-push
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to EC2
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_KEY }}
          script: |
            echo ${{ secrets.GITHUB_TOKEN }} | docker login ghcr.io -u ${{ github.actor }} --password-stdin
            cd my-app
            docker compose pull
            docker compose up -d
            docker image prune -f

```

### 2.5. Backend CORS 설정 (`WebMvcConfig.java`)

프론트엔드 서버(EC2)가 백엔드 API를 호출할 수 있도록 허용

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://43.203.2.175:3000" // 서울 서버 IP 추가
                )
                .allowedOriginPatterns("https://*.vercel.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

```

---

## 3. 서버 필수 명령어 모음 (Cheat Sheet)

### 3.1. 초기 서버 세팅

```bash
# Docker 설치
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Swap 메모리 설정 (t2.micro 멈춤 방지)
sudo fallocate -l 2G /swapfile && sudo chmod 600 /swapfile
sudo mkswap /swapfile && sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

```

### 3.2. 유지보수 및 디버깅

```bash
# 최신 버전으로 재배포 (수동)
docker compose pull && docker compose up -d

# 백엔드 로그 확인 (실시간)
docker logs -f --tail 100 app-backend

# 프론트엔드 로그 확인
docker logs -f app-frontend

# 환경변수 파일 수정
nano .env

# 컨테이너 상태 확인
docker ps -a

```

---

## 4. 트러블슈팅 히스토리 (Troubleshooting Log)

프로젝트 진행 중 발생했던 주요 이슈와 해결 방법

### Issue 1. Localhost Connection Refused (프론트엔드)

* **증상:** 배포 후 브라우저에서 요청 시 `POST http://localhost:8080/...` 에러 발생.
* **원인:** Next.js는 빌드 시점에 환경 변수를 고정(Inlining)하는데, 빌드 시 서버 IP가 아닌 기본값(localhost)이 들어감.
* **해결:**
1. `Dockerfile`에 `ARG NEXT_PUBLIC_API_BASE_URL` 추가.
2. `deploy.yml`에서 `build-args`로 공인 IP 주입.
3. `frontend`에서 환경변수를 우선 사용하도록 코드 수정.



### Issue 2. Backend Container Crash (백엔드)

* **증상:** 백엔드 컨테이너가 시작 직후 계속 죽음 (`docker ps` 가동 시간 초기화). 로그에 `PlaceholderResolutionException` 발생.
* **원인:** `AWS_ACCESS_KEY` 등 필수 환경 변수가 `.env` 파일에는 있었으나, Docker 컨테이너 내부로 주입되지 않음.
* **해결:** `docker-compose.yml`의 backend 서비스에 `env_file: - .env`를 추가하고 `environment` 섹션에 변수들을 명시하여 해결.

### Issue 3. CORS Policy Blocked (네트워크)

* **증상:** API 주소는 맞으나(`43.203...`), 브라우저 콘솔에 `Blocked by CORS policy` 에러 발생.
* **원인:** 백엔드(8080)가 다른 출처인 프론트엔드(3000)의 요청을 보안상 차단함.
* **해결:** `WebMvcConfig.java`에 `http://43.203.2.175:3000`을 허용 Origin으로 추가. (추후 `SecurityConfig`에도 CORS 설정 적용 권장).

### Issue 4. 8080 Port Connection Timeout (방화벽)

* **증상:** 서버는 켜져 있는데 외부에서 접속 불가 (`ERR_CONNECTION_REFUSED`).
* **원인:** AWS EC2 보안 그룹(Security Group)에서 8080 포트가 닫혀 있었음.
* **해결:** AWS 콘솔 > 보안 그룹 > 인바운드 규칙 > 8080 포트(0.0.0.0/0) 개방.
# 1. Builder Stage (빌드 환경)
# gradle 이미지 대신 JDK 이미지를 사용하여 gradlew(래퍼)를 활용하는 것이 가장 호환성이 좋습니다.
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Gradle 캐싱을 위해 설정 파일 먼저 복사
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY gradlew ./

# gradlew 실행 권한 부여 및 의존성 다운로드 (빌드 속도 향상)
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사 및 빌드
COPY src src
# lombok.config가 있다면 복사, 없으면 이 줄은 지워주세요
RUN ./gradlew bootJar -x test --no-daemon

# ---------------------------------------------------

# 2. Runner Stage (실행 환경)
# 실행할 때는 JDK 대신 가벼운 JRE만 있으면 됩니다.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 빌드 결과물 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 노출 및 실행
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
plugins {
    java
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com"
version = "0.0.1-SNAPSHOT"
description = "backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Web (REST API)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Security (인증/인가)
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Validation (@Valid)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // JWT (JJWT)
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // Swagger (springdoc-openapi)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Monitoring (Prometheus + Grafana)
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // DB Drivers (둘 중 필요한 것만 켜도 됨)
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.springframework.boot:spring-boot-h2console") // Spring Boot 4.0 부터 필요한 라이브러리
    runtimeOnly("com.mysql:mysql-connector-j")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    // Mail
    implementation("org.springframework.boot:spring-boot-starter-mail") // 메일서버와 연결해서 메일을 발송하는데 필요한 라이브러리

    // AWS 공식 Java SDK
    implementation("software.amazon.awssdk:s3:2.21.1")

    // Gemini
    implementation("com.google.genai:google-genai:1.32.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

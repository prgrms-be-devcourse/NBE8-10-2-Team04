# 소모품 교체 주기 관리 웹 서비스
> [프로그래머스 데브코스 8기 10회차] 2차 프로젝트

## 프로젝트 배경
<img width="800" height="470" alt="프로젝트_배경" src="https://github.com/user-attachments/assets/6d6a237a-317c-409a-abe3-10b584d29dc1" />


## 기능
1. 소모품 등록
   - 이름, 카테고리, 사진, 시작일, 주기 기록
   - AI가 추천해주는 적정 교체 주기

2. 소모품 조회
   - D-day가 임박한 순서대로 정렬하여 손쉽게 확인
   - 카테고리 필터링
   - 등록, 교체 이력 조회
   - 활성/비활성 상태 관리
   - 소모품 교체: 등록된 주기를 기반으로 새로운 교체일로 변경

3. 교체일 D-Day 시 이메일 알림

4. 모든 소모품 이력을 한눈에 조회

5. 사용 통계
   - 카테고리별로 등록된 소모품 개수, 평균 교체 주기
   - 가장 많이 교체한 소모품 순위
  
6. 회원 관리
   - Spring Security, JWT 활용
   - Cookie와 Header에 존재하는 토큰 정보에 따라 인증/인가 수행
   - 회원가입, 로그인, 회원정보(이메일, 비밀번호) 변경, 회원탈퇴

## 기술 스택
- Frontend
  - Next.js
- Backend
  - Spring Boot
  - JPA / Hibernate
  - Spring Security
- Database
  - H2
  - AWS S3
- External API
  - Google Gemini API (Gemini 2.5 Flash)
- Monitoring & Test
  - Prometheus
  - Actuator
  - Grafana
  - k6

## 시스템 아키텍처
<img width="1039" height="658" alt="시스템 구성도  04팀_2차 팀프로젝트" src="https://github.com/user-attachments/assets/c148e1a8-0939-4caf-a984-0e6a09b25e53" />

## ERD
<img width="1230" height="730" alt="image" src="https://github.com/user-attachments/assets/9935bb4f-2108-4c7f-8253-a1c600e1892a" />


## API 명세서
[Notion API 명세서 바로가기](https://www.notion.so/API-2e815a01205481368ebaeff99a878694)


## 팀원 소개
> Team04 소모품을 지키는 데브레인저

|이름|역할|담당 업무|
|--|--|--|
|최승혁|팀장|프로젝트 총괄, 아이템 기능|
|김민지A|팀원|아이템 기능, Gemini API|
|김민지B|팀원|아이템 기능, 와이어프레임 디자인|
|송찬의|팀원|회원 기능, 모니터링/테스트 툴 도입|
|유재원|팀원|회원 기능|


# 🏢 회의실 예약 시스템

## 프로젝트 개요 및 기술 스택/버전

언어: Java  
프레임워크: Spring Boot 3.5.4  
데이터베이스: MySQL 8 (docker 컨테이너)  
ORM: Spring Data JPA  
API 문서화: Swagger (OpenAPI 3.0) - springdoc-openapi  
컨테이너: Docker + Docker Compose  
테스트: JUnit 5 (단위/통합 테스트)  
빌드: Gradle

## docker-compose up 실행 방법

```shell
# docker-compose 빌드 후 실행
docker-compose up --build -d

# docker-compose 실행 중지
docker-compose down -v
```

## Swagger UI 접속 방법

```url
http://localhost:8080/docs
```

## 테스트 실행 방법

```shell
./gradlew clean test
```

## 설계 결정 사항

- 예약 동시성 문제
    - 비관적 락
- 테스트 코드 환경
    - 실제와 동일한 환경에서 테스트 하고자 `TestContainer` 이용
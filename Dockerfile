FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew build --no-daemon || return 0

COPY . .
#RUN SPRING_PROFILES_ACTIVE=test SPRING_DATASOURCE_URL_TEST=jdbc:mysql://localhost:3305/test?serverTimezone=Asia/Seoul&characterEncoding=UTF-8 ./gradlew clean test
RUN ./gradlew clean test bootJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew build --no-daemon || return 0

COPY . .
RUN ./gradlew bootJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
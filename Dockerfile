FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /app
COPY . .

RUN gradle dependencies --no-daemon

RUN gradle clean build -x test

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
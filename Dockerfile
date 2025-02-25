# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /manga_reader_app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# Etapa 2: Ejecución
FROM openjdk:21-jdk-slim
WORKDIR /manga_reader_app

ARG APP_NAME=manga-reader
ARG APP_VERSION=0.0.1-SNAPSHOT
ARG JAR_FILE=manga_reader_app/target/${APP_NAME}-${APP_VERSION}.jar

COPY --from=builder ${JAR_FILE} app_manga_reader.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app_manga_reader.jar"]

FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY mechita_web/pom.xml .
RUN mvn dependency:go-offline -B

COPY mechita_web/src ./src

RUN mvn clean package -DskipTests

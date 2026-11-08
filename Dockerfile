```dockerfile
# ETAPA 1: Construcción
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY mechita_web/pom.xml .
RUN mvn dependency:go-offline -B

COPY mechita_web/src ./src

RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```
